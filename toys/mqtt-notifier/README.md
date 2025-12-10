# MQTT 通知器



## 打包

```python
pyinstaller --onefile --noconsole --hidden-import=plyer.platforms.win.notification mqtt_notifier.py

pyinstaller --onefile --hidden-import=plyer.platforms.win.notification mqtt_notifier.py

pyinstaller --onefile --noconsole --hidden-import=plyer.platforms.win.notification --name="MQTT通知器" --icon=app_icon.ico --add-data "app_icon.ico;." mqtt_notifier_gui.py

```



<br />

<br />

## 公共服务器

EMQX 公共 MQTT 服务器

地址：https://www.emqx.com/zh/mqtt-dashboard

<br />

<br />

