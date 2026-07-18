# Java_PaymentSystem💶
> This is a simple Java project: a payment system. It supports three payment methods—Alipay / WechatPay / bank card payment.Allowing users to select their preferred option. Each method includes password protection and security verification, and the system implements data persistence.

# 面向对象编程 OOP 的四大特性
## 继承
## 多态
## 封装
## 抽象

# Java 异常处理详解
## 异常体系结构
Java 所有异常都继承自 `Throwable`，分为两大分支：

```
Throwable
├── Error              (严重错误，程序通常无法/不应处理)
│   ├── OutOfMemoryError
│   └── StackOverflowError
└── Exception           (程序可以处理的异常)
    ├── RuntimeException          (非受检异常 unchecked)
    │   ├── NullPointerException
    │   ├── ArrayIndexOutOfBoundsException
    │   ├── ClassCastException
    │   ├── ArithmeticException
    │   └── NumberFormatException
    └── 其他 Exception 子类        (受检异常 checked)
        ├── IOException
        ├── SQLException
        └── ClassNotFoundException
```

### 受检异常 vs 非受检异常（重点概念）
| 类型 | 是否强制处理 | 典型例子 | 含义 |
|---|---|---|---|
| **受检异常**（Checked Exception） | 必须 `try-catch` 或 `throws` 声明，否则编译不通过 | `IOException`、`SQLException` | 可预见的外部问题（文件不存在、网络中断） |
| **非受检异常**（Unchecked Exception，即 `RuntimeException` 及其子类） | 编译器不强制处理 | `NullPointerException`、`ArithmeticException` | 通常是代码逻辑错误，理论上应该通过写好代码避免，而不是到处 catch |
| **Error** | 不建议捕获处理 | `OutOfMemoryError` | JVM 层面的严重问题，程序通常已无法正常恢复 |

---

## 基本语法：try-catch-finally
```java
public class BasicTryCatch {
    public static void main(String[] args) {
        try {
            int[] arr = {1, 2, 3};
            System.out.println(arr[5]); // 会抛出 ArrayIndexOutOfBoundsException
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("数组越界了: " + e.getMessage());
        } finally {
            System.out.println("无论是否发生异常，这里都会执行");
        }
        System.out.println("程序继续往下走");
    }
}
```
## 多重捕获：catch 多个异常类型

```java
public class MultiCatchDemo {
    public static void main(String[] args) {
        String[] inputs = {"123", "abc", null};

        for (String input : inputs) {
            try {
                int num = Integer.parseInt(input); // 可能抛 NumberFormatException
                System.out.println(100 / num);      // 可能抛 ArithmeticException
            } catch (NumberFormatException | ArithmeticException e) {
                // JDK 7+ 支持用 | 合并捕获多种异常类型（多态处理相同逻辑）
                System.out.println("数字格式或运算错误: " + e.getMessage());
            } catch (NullPointerException e) {
                System.out.println("输入为空: " + e.getMessage());
            }
        }
    }
}
```
---

## try-with-resources：自动关闭资源（现代标准写法）

任何实现了 `AutoCloseable` 接口的对象（如各种 IO 流、数据库连接）都可以用这种写法，编译器会自动在结束时调用 `close()`，等价于自动生成了 `finally { close(); }`，比手动写更安全。

```java
import java.io.*;

public class TryWithResourcesDemo {
    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
            String line = br.readLine();
            System.out.println(line);
        } catch (IOException e) {
            System.out.println("读取文件失败: " + e.getMessage());
        }
        // 不需要手动写 finally { br.close(); }，编译器自动处理
    }
}
```

可以同时管理多个资源，用分号分隔，**关闭顺序与声明顺序相反**（后声明的先关闭）：

```java
try (FileInputStream in = new FileInputStream("in.txt");
     FileOutputStream out = new FileOutputStream("out.txt")) {
    // 使用 in 和 out
} catch (IOException e) {
    e.printStackTrace();
}
```

---

## 抛出异常：throw 与 throws

- **`throw`**：语句，用来实际"抛出"一个异常对象。
- **`throws`**：写在方法签名上，用来"声明"这个方法可能会抛出哪些异常，交给调用者处理。

```java
public class ThrowDemo {
    // 方法签名用 throws 声明可能抛出的受检异常
    static void readConfig(String path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("路径不能为空"); // throw 一个非受检异常
        }
        if (!new java.io.File(path).exists()) {
            throw new IOException("配置文件不存在: " + path); // throw 一个受检异常
        }
        System.out.println("配置加载成功");
    }

    public static void main(String[] args) {
        try {
            readConfig("config.txt");
        } catch (IOException e) {
            System.out.println("捕获异常: " + e.getMessage());
        }
    }
}
```

调用一个声明了 `throws` 受检异常的方法时，调用者必须要么 `try-catch` 处理，要么自己也用 `throws` 继续往上抛，否则编译不通过

---

## 自定义异常
```java
// 自定义受检异常：继承 Exception
class InsufficientGoldException extends Exception {
    public InsufficientGoldException(String message) {
        super(message);
    }
}

// 自定义非受检异常：继承 RuntimeException
class InvalidPlayerStateException extends RuntimeException {
    public InvalidPlayerStateException(String message) {
        super(message);
    }
}

class Player {
    int gold = 50;
    boolean isAlive = true;

    void buyItem(int price) throws InsufficientGoldException {
        if (price > gold) {
            throw new InsufficientGoldException("金币不足，还差 " + (price - gold) + " 金币");
        }
        gold -= price;
        System.out.println("购买成功，剩余金币: " + gold);
    }

    void attack() {
        if (!isAlive) {
            throw new InvalidPlayerStateException("角色已死亡，无法攻击"); // 非受检，不用强制catch
        }
        System.out.println("发起攻击！");
    }
}

public class CustomExceptionDemo {
    public static void main(String[] args) {
        Player player = new Player();
        try {
            player.buyItem(100);
        } catch (InsufficientGoldException e) {
            System.out.println("购买失败: " + e.getMessage());
        }

        player.isAlive = false;
        player.attack(); // 未被捕获，程序会打印堆栈信息后终止
    }
}
```

### 该继承 Exception 还是 RuntimeException？

一个实用判断标准：
- 这个错误**调用者应该被强制处理**、是可以预见并恢复的业务流程的一部分（比如"金币不足"，调用者应该提示用户） → 继承 `Exception`（受检）
- 这个错误**属于编程错误或不应该发生的状态**，调用方大多数时候不会/不需要针对性处理 → 继承 `RuntimeException`（非受检）
- 
---

常用异常方法

```java
try {
    throw new RuntimeException("出错了");
} catch (RuntimeException e) {
    e.getMessage();       // 获取异常描述信息："出错了"W
    e.getCause();          // 获取导致这个异常的原始异常（没有则为 null）
    e.printStackTrace();   // 打印完整调用栈，定位问题源头，调试时最常用
    e.getStackTrace();     // 获取堆栈帧数组，可用于日志记录、自定义格式化输出
    e.toString();          // 类名 + message，例如 "java.lang.RuntimeException: 出错了"
}
```

---

## 速查表
| 需求 | 关键字/写法 |
|---|---|
| 捕获并处理异常 | `try { } catch (XxxException e) { }` |
| 无论是否异常都执行的清理代码 | `finally { }` |
| 自动关闭资源（推荐） | `try (Resource r = ...) { }` |
| 主动抛出异常 | `throw new XxxException("message")` |
| 声明方法可能抛出受检异常 | `void method() throws XxxException` |
| 捕获多种类型用同一逻辑处理 | `catch (TypeA \| TypeB e) { }` |
| 保留原始异常信息 | `throw new NewException("msg", originalException)` |
| 自定义受检异常 | `class MyException extends Exception` |
| 自定义非受检异常 | `class MyException extends RuntimeException` |
