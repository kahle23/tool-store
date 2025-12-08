import paho.mqtt.client as mqtt
import json
import time
import sys
import os
import threading
import logging
from datetime import datetime
from plyer import notification
import tkinter as tk
from tkinter import ttk, scrolledtext, messagebox
import pystray
from PIL import Image, ImageDraw
import queue
import configparser
import webbrowser
import base64
import tempfile


# 配置日志
def setup_logging():
    if getattr(sys, 'frozen', False):
        base_dir = os.path.dirname(sys.executable)
    else:
        base_dir = os.path.dirname(os.path.abspath(__file__))

    log_file = os.path.join(base_dir, 'mqtt_notifier.log')

    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(levelname)s - %(message)s',
        handlers=[
            logging.FileHandler(log_file, encoding='utf-8'),
            logging.StreamHandler()
        ]
    )
    return logging.getLogger("MQTTNotifier")


class MQTTNotifierApp:
    def __init__(self, root):
        self.root = root
        self.root.title("MQTT通知器")
        self.root.geometry("800x600")

        # 日志记录器
        self.logger = setup_logging()

        # 设置窗口图标
        self.set_icon()

        # 消息队列
        self.message_queue = queue.Queue()

        # MQTT客户端
        self.client = None
        self.connected = False

        # 配置
        self.config = configparser.ConfigParser()
        self.config_file = "mqtt_config.ini"
        self.load_config()

        # 系统托盘
        self.tray_icon = None
        self.create_system_tray()

        # 创建界面
        self.create_widgets()

        # 处理消息队列
        self.root.after(100, self.process_queue)

        # 窗口关闭事件
        self.root.protocol("WM_DELETE_WINDOW", self.minimize_to_tray)

        # 连接状态
        self.connection_status = "未连接"

        # 尝试自动连接
        if self.auto_connect_var.get():
            self.root.after(1000, self.connect_mqtt)

    def set_icon(self):
        """设置窗口图标 - 使用本地PNG图片"""
        icon_path = self.get_icon_path()

        try:
            if icon_path and os.path.exists(icon_path):
                # 使用PIL打开PNG图片
                img = Image.open(icon_path)

                # 将PIL图片转换为PhotoImage
                from PIL import ImageTk
                photo = ImageTk.PhotoImage(img)

                # 设置窗口图标
                self.root.iconphoto(True, photo)

                # 保存引用，防止被垃圾回收
                self.icon_photo = photo
                self.logger.info(f"已设置窗口图标: {icon_path}")
            else:
                # 如果没有找到PNG图片，创建一个默认图标
                self.create_default_icon()
        except Exception as e:
            self.logger.warning(f"无法设置窗口图标: {e}")
            # 如果失败，创建默认图标
            self.create_default_icon()

    def get_icon_path(self):
        """获取图标路径 - 支持打包后的程序"""
        # 支持的系统图标文件名
        icon_files = [
            "mqtt_icon.ico", "icon.ico", "logo.ico", "app_icon.ico",
            "mqtt_icon.png", "icon.png", "logo.png", "app_icon.png"
        ]

        # 判断是否被打包
        if getattr(sys, 'frozen', False):
            # 如果是打包后的程序
            base_dir = sys._MEIPASS
        else:
            # 如果是源代码运行
            base_dir = os.path.dirname(os.path.abspath(__file__))

        # 先检查打包目录
        for icon_file in icon_files:
            full_path = os.path.join(base_dir, icon_file)
            if os.path.exists(full_path):
                return full_path

        # 再检查当前工作目录
        for icon_file in icon_files:
            if os.path.exists(icon_file):
                return icon_file

        # 检查 assets 子目录
        assets_dir = os.path.join(base_dir, "assets")
        if os.path.exists(assets_dir):
            for icon_file in icon_files:
                full_path = os.path.join(assets_dir, icon_file)
                if os.path.exists(full_path):
                    return full_path

        return None

    def create_default_icon(self):
        """创建默认图标（当找不到PNG图片时）"""
        try:
            # 创建一个简单的MQTT图标
            from PIL import Image, ImageDraw

            # 创建一个64x64的白色背景图片
            image = Image.new('RGB', (64, 64), (255, 255, 255))
            draw = ImageDraw.Draw(image)

            # 绘制MQTT图标
            # 外框
            draw.rectangle([5, 5, 59, 59], outline='#0066CC', width=3)

            # 中心点
            draw.ellipse([25, 25, 39, 39], fill='#0066CC')

            # 四个连接点
            draw.ellipse([15, 15, 20, 20], fill='#0066CC')  # 左上
            draw.ellipse([44, 15, 49, 20], fill='#0066CC')  # 右上
            draw.ellipse([15, 44, 20, 49], fill='#0066CC')  # 左下
            draw.ellipse([44, 44, 49, 54], fill='#0066CC')  # 右下

            # 连接线
            draw.line([18, 18, 32, 32], fill='#0066CC', width=2)
            draw.line([46, 18, 32, 32], fill='#0066CC', width=2)
            draw.line([18, 46, 32, 32], fill='#0066CC', width=2)
            draw.line([46, 46, 32, 32], fill='#0066CC', width=2)

            # 转换为PhotoImage
            from PIL import ImageTk
            photo = ImageTk.PhotoImage(image)

            # 设置窗口图标
            self.root.iconphoto(True, photo)

            # 保存引用
            self.icon_photo = photo
            self.default_icon_image = image  # 保存用于系统托盘

            self.logger.info("已创建默认图标")

        except Exception as e:
            self.logger.error(f"创建默认图标失败: {e}")

    def create_system_tray(self):
        """创建系统托盘图标 - 使用本地PNG图片，支持双击打开"""
        try:
            icon_path = self.get_icon_path()

            if icon_path and os.path.exists(icon_path):
                # 使用本地PNG图片
                image = Image.open(icon_path)
                self.logger.info(f"使用PNG图片创建系统托盘图标: {icon_path}")
            else:
                # 使用默认图标
                if hasattr(self, 'default_icon_image'):
                    image = self.default_icon_image
                else:
                    # 创建一个简单的默认图标
                    image = Image.new('RGB', (64, 64), (255, 255, 255))
                    draw = ImageDraw.Draw(image)
                    draw.rectangle([10, 10, 54, 54], outline='#0066CC', width=3)
                    draw.ellipse([20, 20, 44, 44], fill='#0066CC')
                    self.logger.info("使用默认图标创建系统托盘图标")

            # 托盘菜单
            menu = (
                pystray.MenuItem("显示主窗口", self.show_window),
                pystray.MenuItem("断开连接", self.disconnect_mqtt),
                pystray.MenuItem("退出", self.quit_app)
            )

            # 创建托盘图标
            self.tray_icon = pystray.Icon("mqtt_notifier", image, "MQTT通知器", menu)

            # 设置双击事件
            def on_double_click(icon, item):
                """双击托盘图标事件"""
                self.show_window()

            # 注意：pystray 默认不支持双击事件，我们需要通过其他方式实现
            # 使用菜单项来实现双击效果
            # 在Windows上，双击托盘图标会执行第一个菜单项
            # 所以我们将"显示主窗口"作为第一个菜单项

            # 在单独的线程中运行托盘图标
            tray_thread = threading.Thread(target=self.tray_icon.run, daemon=True)
            tray_thread.start()

        except Exception as e:
            self.logger.error(f"创建系统托盘失败: {e}")

    def create_widgets(self):
        """创建GUI界面"""
        # 创建主框架
        main_frame = ttk.Frame(self.root, padding="10")
        main_frame.grid(row=0, column=0, sticky=(tk.W, tk.E, tk.N, tk.S))

        # 配置网格权重
        self.root.columnconfigure(0, weight=1)
        self.root.rowconfigure(0, weight=1)
        main_frame.columnconfigure(1, weight=1)

        # 连接配置区域
        ttk.Label(main_frame, text="MQTT连接配置", font=("Arial", 12, "bold")).grid(
            row=0, column=0, columnspan=3, pady=(0, 10), sticky=tk.W
        )

        # 服务器地址
        ttk.Label(main_frame, text="服务器:").grid(row=1, column=0, sticky=tk.W, pady=5)
        self.server_var = tk.StringVar(value=self.config.get('MQTT', 'broker', fallback='broker.emqx.io'))
        server_entry = ttk.Entry(main_frame, textvariable=self.server_var, width=30)
        server_entry.grid(row=1, column=1, sticky=(tk.W, tk.E), pady=5, padx=(5, 0))

        # 端口
        ttk.Label(main_frame, text="端口:").grid(row=2, column=0, sticky=tk.W, pady=5)
        self.port_var = tk.StringVar(value=self.config.get('MQTT', 'port', fallback='1883'))
        port_entry = ttk.Entry(main_frame, textvariable=self.port_var, width=10)
        port_entry.grid(row=2, column=1, sticky=tk.W, pady=5, padx=(5, 0))

        # 主题
        ttk.Label(main_frame, text="主题:").grid(row=3, column=0, sticky=tk.W, pady=5)
        self.topic_var = tk.StringVar(value=self.config.get('MQTT', 'topic', fallback='test/th/jpom/hello'))
        topic_entry = ttk.Entry(main_frame, textvariable=self.topic_var, width=30)
        topic_entry.grid(row=3, column=1, sticky=(tk.W, tk.E), pady=5, padx=(5, 0))

        # 用户名
        ttk.Label(main_frame, text="用户名:").grid(row=4, column=0, sticky=tk.W, pady=5)
        self.username_var = tk.StringVar(value=self.config.get('MQTT', 'username', fallback=''))
        username_entry = ttk.Entry(main_frame, textvariable=self.username_var, width=30)
        username_entry.grid(row=4, column=1, sticky=(tk.W, tk.E), pady=5, padx=(5, 0))

        # 密码
        ttk.Label(main_frame, text="密码:").grid(row=5, column=0, sticky=tk.W, pady=5)
        self.password_var = tk.StringVar(value=self.config.get('MQTT', 'password', fallback=''))
        password_entry = ttk.Entry(main_frame, textvariable=self.password_var, width=30, show="*")
        password_entry.grid(row=5, column=1, sticky=tk.W, pady=5, padx=(5, 0))

        # 连接按钮
        button_frame = ttk.Frame(main_frame)
        button_frame.grid(row=6, column=0, columnspan=3, pady=10, sticky=tk.W)

        self.connect_button = ttk.Button(button_frame, text="连接", command=self.connect_mqtt)
        self.connect_button.grid(row=0, column=0, padx=(0, 5))

        self.disconnect_button = ttk.Button(button_frame, text="断开", command=self.disconnect_mqtt, state=tk.DISABLED)
        self.disconnect_button.grid(row=0, column=1, padx=(0, 5))

        # 自动连接选项
        self.auto_connect_var = tk.BooleanVar(value=self.config.getboolean('MQTT', 'auto_connect', fallback=False))
        auto_connect_check = ttk.Checkbutton(
            button_frame, text="启动时自动连接", variable=self.auto_connect_var
        )
        auto_connect_check.grid(row=0, column=2, padx=(20, 5))

        # 连接状态
        self.status_label = ttk.Label(main_frame, text="状态: 未连接", foreground="red")
        self.status_label.grid(row=7, column=0, columnspan=3, pady=(5, 10), sticky=tk.W)

        # 消息显示区域
        ttk.Label(main_frame, text="接收到的消息", font=("Arial", 12, "bold")).grid(
            row=8, column=0, columnspan=3, pady=(10, 5), sticky=tk.W
        )

        # 消息文本框
        self.message_text = scrolledtext.ScrolledText(main_frame, width=80, height=20)
        self.message_text.grid(row=9, column=0, columnspan=3, sticky=(tk.W, tk.E, tk.N, tk.S), pady=(0, 10))

        # 按钮区域
        bottom_button_frame = ttk.Frame(main_frame)
        bottom_button_frame.grid(row=10, column=0, columnspan=3, pady=5, sticky=tk.W)

        ttk.Button(bottom_button_frame, text="清空消息", command=self.clear_messages).grid(row=0, column=0, padx=(0, 5))
        ttk.Button(bottom_button_frame, text="保存配置", command=self.save_config).grid(row=0, column=1, padx=(0, 5))
        ttk.Button(bottom_button_frame, text="最小化到托盘", command=self.minimize_to_tray).grid(row=0, column=2,
                                                                                                 padx=(0, 5))
        ttk.Button(bottom_button_frame, text="测试通知", command=self.test_notification).grid(row=0, column=3,
                                                                                              padx=(0, 5))

        # 设置行权重
        main_frame.rowconfigure(9, weight=1)
        main_frame.columnconfigure(1, weight=1)

    def load_config(self):
        """加载配置文件"""
        try:
            if os.path.exists(self.config_file):
                self.config.read(self.config_file)
                if 'MQTT' not in self.config:
                    self.config['MQTT'] = {}
        except Exception as e:
            self.logger.error(f"加载配置失败: {e}")
            self.config['MQTT'] = {}

    def save_config(self):
        """保存配置到文件"""
        try:
            self.config['MQTT'] = {
                'broker': self.server_var.get(),
                'port': self.port_var.get(),
                'topic': self.topic_var.get(),
                'username': self.username_var.get(),
                'password': self.password_var.get(),
                'auto_connect': str(self.auto_connect_var.get())
            }

            with open(self.config_file, 'w') as f:
                self.config.write(f)

            self.logger.info("配置已保存")
            messagebox.showinfo("成功", "配置已保存")
        except Exception as e:
            self.logger.error(f"保存配置失败: {e}")
            messagebox.showerror("错误", f"保存配置失败: {e}")

    def connect_mqtt(self):
        """连接MQTT服务器"""
        if self.connected:
            messagebox.showwarning("警告", "已经连接到MQTT服务器")
            return

        broker = self.server_var.get()
        port_str = self.port_var.get()
        topic = self.topic_var.get()
        username = self.username_var.get()
        password = self.password_var.get()

        # 验证输入
        if not broker or not port_str or not topic:
            messagebox.showerror("错误", "请填写服务器、端口和主题")
            return

        try:
            port = int(port_str)
        except ValueError:
            messagebox.showerror("错误", "端口必须是数字")
            return

        # 创建MQTT客户端
        self.client = mqtt.Client()

        # 设置回调函数
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        self.client.on_disconnect = self.on_disconnect

        # 设置认证
        if username:
            self.client.username_pw_set(username, password)

        # 连接MQTT服务器
        try:
            self.client.connect(broker, port, 60)
            self.client.loop_start()

            # 更新UI状态
            self.connect_button.config(state=tk.DISABLED)
            self.disconnect_button.config(state=tk.NORMAL)
            self.status_label.config(text=f"连接中...", foreground="orange")
            self.logger.info(f"正在连接 {broker}:{port}")

        except Exception as e:
            self.logger.error(f"连接失败: {e}")
            messagebox.showerror("连接失败", f"无法连接到MQTT服务器: {e}")
            self.client = None

    def disconnect_mqtt(self):
        """断开MQTT连接"""
        if self.client and self.connected:
            try:
                self.client.loop_stop()
                self.client.disconnect()
                self.connected = False

                # 更新UI状态
                self.connect_button.config(state=tk.NORMAL)
                self.disconnect_button.config(state=tk.DISABLED)
                self.status_label.config(text="状态: 已断开", foreground="red")
                self.logger.info("MQTT连接已断开")

            except Exception as e:
                self.logger.error(f"断开连接失败: {e}")

    def on_connect(self, client, userdata, flags, rc):
        """连接回调函数"""
        if rc == 0:
            self.connected = True
            topic = self.topic_var.get()
            client.subscribe(topic)

            # 在主线程中更新UI
            self.root.after(0, self.update_connection_status, True, f"已连接到: {self.server_var.get()}")
            self.logger.info(f"已连接到 {self.server_var.get()}:{self.port_var.get()}")
            self.logger.info(f"已订阅主题: {topic}")

            # 显示通知
            self.show_notification("MQTT通知器", f"已连接到服务器并开始监听主题: {topic}")

        else:
            self.root.after(0, self.update_connection_status, False, f"连接失败: {rc}")
            self.logger.error(f"连接失败: {rc}")

    def on_message(self, client, userdata, msg):
        """消息回调函数"""
        try:
            payload = msg.payload.decode('utf-8')
            timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

            # 记录日志
            self.logger.info(f"收到消息: {msg.topic}\n{payload}")

            # 将消息添加到队列
            self.message_queue.put((timestamp, msg.topic, payload))

        except Exception as e:
            self.logger.error(f"处理消息错误: {e}")

    def on_disconnect(self, client, userdata, rc):
        """断开连接回调函数"""
        if rc != 0:
            self.logger.warning("连接断开，尝试重连...")
            self.root.after(0, self.update_connection_status, False, "连接断开，尝试重连...")
            threading.Timer(5, self.reconnect).start()

    def reconnect(self):
        """重新连接"""
        if self.client and not self.connected:
            try:
                self.client.reconnect()
            except:
                self.logger.error("重连失败")

    def process_queue(self):
        """处理消息队列"""
        try:
            while not self.message_queue.empty():
                timestamp, topic, payload = self.message_queue.get_nowait()

                # 格式化消息
                formatted_msg = f"[{timestamp}] {topic}\n"

                try:
                    json_msg = json.loads(payload)
                    formatted_msg += json.dumps(json_msg, ensure_ascii=False, indent=2) + "\n"
                except:
                    formatted_msg += payload + "\n"

                formatted_msg += "-" * 50 + "\n"

                # 更新消息显示
                self.message_text.insert(tk.END, formatted_msg)
                self.message_text.see(tk.END)

                # 显示通知
                title = f"MQTT - {topic}"
                try:
                    json_msg = json.loads(payload)
                    message = json.dumps(json_msg, ensure_ascii=False, indent=2)
                    if len(message) > 200:
                        message = message[:200] + "..."
                except:
                    message = payload[:200] + "..." if len(payload) > 200 else payload

                self.show_notification(title, message)

        except queue.Empty:
            pass

        # 每100ms检查一次队列
        self.root.after(100, self.process_queue)

    def update_connection_status(self, connected, message):
        """更新连接状态"""
        self.connected = connected
        if connected:
            self.status_label.config(text=f"状态: {message}", foreground="green")
            self.connect_button.config(state=tk.DISABLED)
            self.disconnect_button.config(state=tk.NORMAL)
        else:
            self.status_label.config(text=f"状态: {message}", foreground="red")
            self.connect_button.config(state=tk.NORMAL)
            self.disconnect_button.config(state=tk.DISABLED)

    def show_notification(self, title, message, duration=5):
        """显示系统通知 - 包含图标"""
        try:
            # 获取图标路径
            icon_path = self.get_icon_path()

            # 使用plyer显示通知，如果找到图标就添加图标
            if icon_path and os.path.exists(icon_path):
                notification.notify(
                    title=title,
                    message=message,
                    timeout=duration,
                    app_name="MQTT通知器",
                    app_icon=icon_path
                )
            else:
                # 如果没有找到图标，显示不带图标的通知
                notification.notify(
                    title=title,
                    message=message,
                    timeout=duration,
                    app_name="MQTT通知器"
                )

        except Exception as e:
            self.logger.error(f"通知显示失败: {e}")
            # 备用方案：在控制台打印
            print(f"通知: {title} - {message}")

    def clear_messages(self):
        """清空消息显示"""
        self.message_text.delete(1.0, tk.END)

    def test_notification(self):
        """测试通知功能"""
        self.show_notification("测试通知", "这是一个测试通知，用于验证通知功能是否正常工作。")

    def minimize_to_tray(self):
        """最小化到系统托盘"""
        self.root.withdraw()
        if self.tray_icon:
            self.tray_icon.visible = True

    def show_window(self, icon=None, item=None):
        """显示主窗口"""
        self.root.deiconify()
        self.root.attributes('-topmost', True)
        self.root.attributes('-topmost', False)
        if self.tray_icon:
            self.tray_icon.visible = False

    def quit_app(self, icon=None, item=None):
        """退出应用程序"""
        if self.client and self.connected:
            self.disconnect_mqtt()

        if self.tray_icon:
            self.tray_icon.stop()

        self.root.quit()
        self.root.destroy()


def main():
    """主函数"""
    # 检查依赖
    try:
        import paho.mqtt.client
        from plyer import notification
    except ImportError as e:
        print(f"缺少依赖库: {e}")
        print("请安装: pip install -i https://pypi.tuna.tsinghua.edu.cn/simple/ paho-mqtt plyer pystray pillow")
        sys.exit(1)

    # 创建主窗口
    root = tk.Tk()

    # 设置窗口样式
    root.style = ttk.Style()
    root.style.theme_use('clam')

    # 创建应用程序
    app = MQTTNotifierApp(root)

    # 运行主循环
    root.mainloop()


if __name__ == "__main__":
    main()

# 打包说明:
# 1. 安装依赖:
#    pip install paho-mqtt plyer pystray pillow
#
# 2. 将你的PNG图标文件命名为以下名称之一，放在程序同目录下:
#    - mqtt_icon.png
#    - icon.png
#    - logo.png
#    - app_icon.png
#
# 3. 使用PyInstaller打包(包含图标文件):
#    pyinstaller --onefile --noconsole --hidden-import=plyer.platforms.win.notification --name="MQTT通知器" --icon=app_icon.ico --add-data "app_icon.ico;." mqtt_notifier_gui.py
#
# 注意: 如果你想要在打包后的可执行文件中包含PNG图标，可以使用--add-data参数

