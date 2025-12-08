import paho.mqtt.client as mqtt
import json
import time
import sys
import os
import threading
import logging
from datetime import datetime
from plyer import notification


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


class MQTTNotifier:
    def __init__(self):
        self.logger = setup_logging()

        # MQTT配置
        self.broker = os.getenv('MQTT_BROKER', 'broker.emqx.io')
        self.port = int(os.getenv('MQTT_PORT', '1883'))
        self.topic = os.getenv('MQTT_TOPIC', 'test/th/jpom/hello')
        self.username = os.getenv('MQTT_USERNAME')
        self.password = os.getenv('MQTT_PASSWORD')

        # 创建MQTT客户端
        self.client = mqtt.Client()
        self.setup_callbacks()

        if self.username and self.password:
            self.client.username_pw_set(self.username, self.password)

    def setup_callbacks(self):
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        self.client.on_disconnect = self.on_disconnect

    def on_connect(self, client, userdata, flags, rc):
        if rc == 0:
            self.logger.info(f"已连接到 {self.broker}:{self.port}")
            client.subscribe(self.topic)
            self.show_notification("MQTT通知器", f"已开始监听主题: {self.topic}")
        else:
            self.logger.error(f"连接失败: {rc}")

    def on_message(self, client, userdata, msg):
        try:
            payload = msg.payload.decode('utf-8')
            self.logger.info(f"收到消息: {msg.topic}\n{payload}\n")

            # 处理消息内容
            title = f"MQTT - {msg.topic}"
            try:
                json_msg = json.loads(payload)
                message = json.dumps(json_msg, ensure_ascii=False, indent=2)
                if len(message) > 200:
                    message = message[:200] + "..."
            except:
                message = payload[:200] + "..." if len(payload) > 200 else payload

            self.show_notification(title, message)
        except Exception as e:
            self.logger.error(f"处理消息错误: {e}")

    def on_disconnect(self, client, userdata, rc):
        if rc != 0:
            self.logger.warning("连接断开，尝试重连...")
            threading.Timer(5, self.reconnect).start()

    def reconnect(self):
        try:
            self.client.reconnect()
        except:
            self.logger.error("重连失败")

    def show_notification(self, title, message, duration=180):
        """显示通知 - 使用plyer"""
        try:
            # 使用plyer显示通知
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

    def start(self):
        try:
            self.client.connect(self.broker, self.port, 60)
            self.client.loop_start()
            self.logger.info("MQTT通知器已启动")

            # 保持程序运行
            while True:
                time.sleep(1)
        except KeyboardInterrupt:
            self.logger.info("用户中断程序")
        except Exception as e:
            self.logger.error(f"启动失败: {e}")
        finally:
            self.client.loop_stop()
            self.client.disconnect()


if __name__ == "__main__":
    # 安装依赖提示
    try:
        import paho.mqtt.client
        from plyer import notification
    except ImportError as e:
        print(f"缺少依赖库: {e}")
        print("请安装: pip install -i https://pypi.tuna.tsinghua.edu.cn/simple/ paho-mqtt plyer")
        sys.exit(1)

    notifier = MQTTNotifier()
    notifier.start()

# pyinstaller --onefile --noconsole --hidden-import=plyer.platforms.win.notification mqtt_notifier.py
# pyinstaller --onefile --hidden-import=plyer.platforms.win.notification mqtt_notifier.py
