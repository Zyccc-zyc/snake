# Java 贪吃蛇

使用 Java Swing 实现的桌面小游戏，兼容 JDK 8 及以上版本，无第三方依赖。

## 运行

在项目目录打开终端，确认 `java -version` 和 `javac -version` 可用，然后执行：

```sh
mkdir build
javac -encoding UTF-8 -d build src/SnakeGame.java
java -cp build SnakeGame
```

也可以在 Windows 双击 `run.bat`，或在 macOS / Linux 执行 `sh run.sh`。
需要支持桌面图形界面的环境。

## 操作与规则

| 按键 | 功能 |
| --- | --- |
| 空格 | 开始、暂停或继续 |
| 方向键 / WASD | 改变方向 |
| Enter | 重新开始 |

- 吃到食物增加 10 分并增长一格，随得分逐渐加速。
- 撞墙或撞到身体结束游戏，填满棋盘则获胜。
- 禁止直接反向移动，每个游戏节拍最多接受一次转向。
- 窗口失去焦点时自动暂停；最高分仅在本次运行中保留。
- 界面采用英文提示，避免不同平台的中文字体兼容问题。

## 验证

```sh
javac -encoding UTF-8 -d build src/SnakeGame.java test/SnakeGameTest.java
java -Djava.awt.headless=true -cp build SnakeGameTest
```

测试覆盖暂停、吃食物、转向限制、碰撞、重开、移入正在腾出的尾格、满盘胜利和 Swing 面板离屏绘制。游戏逻辑和绘制均在 Swing 事件线程驱动，无需 Maven 或 Gradle。
