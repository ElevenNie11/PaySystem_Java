# Java_PaymentSystem💶
> This is a simple Java project: a payment system. It supports three payment methods—Alipay / WechatPay / bank card payment.Allowing users to select their preferred option. Each method includes password protection and security verification, and the system implements data persistence.

# 面向对象编程 OOP 的四大特性
## 继承
## 多态
## 封装
## 抽象

# Java 异常处理详解

异常处理是 Java 健壮性设计的核心机制之一。无论是写游戏（网络掉线、存档损坏、资源加载失败）还是写后端（数据库连接失败、参数非法），都离不开异常处理
---

## 一、异常体系结构

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

这是初学者最容易混淆的点：**受检异常是编译器强制你处理的，非受检异常不是**。

---

## 二、基本语法：try-catch-finally

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

### finally 的执行时机

`finally` 块几乎总会执行——即便 `try` 或 `catch` 里有 `return`：

```java
public class FinallyDemo {
    static int test() {
        try {
            return 1;
        } finally {
            System.out.println("finally 依然会执行"); // 会打印
        }
    }

    public static void main(String[] args) {
        System.out.println(test()); // 先打印 "finally 依然会执行"，再打印 1
    }
}
```

**唯一的例外**：如果调用了 `System.exit()`，或者 JVM 崩溃/被强制杀死，`finally` 不会执行。

**坑点**：不要在 `finally` 里再写 `return`，它会**覆盖** `try`/`catch` 里的返回值，是非常容易踩的隐蔽 bug：

```java
static int bad() {
    try {
        return 1;
    } finally {
        return 2; // 危险！最终返回的是 2，而不是 1
    }
}
```

---

## 三、多重捕获：catch 多个异常类型

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

**注意捕获顺序**：子类异常必须写在父类异常前面，否则编译报错（因为父类 catch 块会"拦截"所有子类，导致后面的子类 catch 永远走不到）：

```java
try {
    // ...
} catch (Exception e) {           // 父类在前 -> 编译错误！
    // ...
} catch (NullPointerException e) { // 子类在后，永远不可达
    // ...
}
```

---

## 四、try-with-resources：自动关闭资源（现代标准写法）

任何实现了 `AutoCloseable` 接口的对象（如各种 IO 流、数据库连接）都可以用这种写法，编译器会自动在结束时调用 `close()`，等价于自动生成了 `finally { close(); }`，比手动写更简洁安全。

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

## 五、抛出异常：throw 与 throws

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

调用一个声明了 `throws` 受检异常的方法时，调用者必须要么 `try-catch` 处理，要么自己也用 `throws` 继续往上抛，否则编译不通过。

---

## 六、自定义异常

在实际项目中（尤其游戏开发的业务逻辑、后端的业务校验），经常需要自定义异常类，让错误信息更贴合业务语义。

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
- 这个错误**调用者应该被强制处理**、是可以预见并恢复的业务流程的一部分（比如"金币不足"，调用者应该提示用户） → 继承 `Exception`（受检）。
- 这个错误**属于编程错误或不应该发生的状态**，调用方大多数时候不会/不需要针对性处理 → 继承 `RuntimeException`（非受检）。

现代 Java 实践（包括 Spring 等主流框架）总体上更倾向于少用受检异常，因为过多的 `throws` 声明会污染方法签名、强迫调用者写很多样板 catch 代码。

---

## 七、异常链：保留原始异常信息

有时候你捕获了一个底层异常，想抛出一个更贴合业务语义的新异常，但又不想丢失原始异常的堆栈信息，这时候要用**异常链**（把原始异常作为 cause 传进去）：

```java
public class ExceptionChainDemo {
    static void loadSaveFile() {
        try {
            throw new java.io.IOException("磁盘读取失败");
        } catch (java.io.IOException e) {
            // 把原始异常 e 作为 cause 传入新异常，保留完整堆栈链
            throw new RuntimeException("加载存档失败", e);
        }
    }

    public static void main(String[] args) {
        try {
            loadSaveFile();
        } catch (RuntimeException e) {
            System.out.println("业务层错误: " + e.getMessage());
            System.out.println("根本原因: " + e.getCause().getMessage()); // 磁盘读取失败
            e.printStackTrace(); // 会打印完整的两层堆栈，方便调试
        }
    }
}
```

如果不做异常链、直接 `throw new RuntimeException("加载存档失败")`，原始的"磁盘读取失败"信息就永久丢失了，排查问题会很痛苦——这是实际项目中非常重要的实践。

---

## 八、常用异常方法

```java
try {
    throw new RuntimeException("出错了");
} catch (RuntimeException e) {
    e.getMessage();       // 获取异常描述信息："出错了"
    e.getCause();          // 获取导致这个异常的原始异常（没有则为 null）
    e.printStackTrace();   // 打印完整调用栈，定位问题源头，调试时最常用
    e.getStackTrace();     // 获取堆栈帧数组，可用于日志记录、自定义格式化输出
    e.toString();          // 类名 + message，例如 "java.lang.RuntimeException: 出错了"
}
```

---

## 九、常见坑与最佳实践

### 1. 不要用异常处理正常业务逻辑（性能陷阱）

```java
// 错误示范：用异常控制正常流程，性能极差
try {
    while (true) {
        array[i++] = 0; // 用抛出 ArrayIndexOutOfBoundsException 来判断循环何时结束
    }
} catch (ArrayIndexOutOfBoundsException e) { }
```
异常的构造成本（尤其是填充堆栈信息）比普通条件判断高得多，正常的循环终止应该用 `for`/`while` 的条件表达式判断，而不是"等它抛异常"。

### 2. 不要吞掉异常（空 catch 块）

```java
try {
    riskyOperation();
} catch (Exception e) {
    // 什么都不做 —— 极其危险，出了问题却毫无线索排查
}
```
至少应该打印日志或做基本处理：

```java
} catch (Exception e) {
    logger.error("操作失败", e); // 保留堆栈信息，方便排查
}
```

### 3. 不要捕获过于宽泛的 `Exception`（除非在最外层兜底）

```java
try {
    parseUserInput();
} catch (Exception e) { // 太宽泛，可能把NullPointerException这类真正的bug也悄悄吞掉
    System.out.println("解析失败");
}
```
应该精确捕获你预期会发生的具体异常类型，让意料之外的异常照常暴露出来，便于发现代码本身的 bug。

### 4. finally 中避免再抛异常或 return（前面已举例说明）

### 5. 资源关闭优先用 try-with-resources，而不是手写 finally 关闭

老式写法容易漏掉判空、或者 close() 本身又抛异常导致覆盖了原始异常，`try-with-resources` 从语言层面帮你规避了这些坑。

---

## 十、速查表

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

---

如果你之后往游戏开发方向深入，会发现异常处理思路是通用的：C# 的 `try-catch-finally` / `using` 语句、Unity 里处理资源加载失败，本质上和 Java 这套逻辑几乎一一对应，理解透了迁移会很快。真正的功力体现在"该用受检异常还是非受检异常""异常边界该设在哪一层""日志怎么记录才便于排查"这些设计判断上，而不只是语法本身。
