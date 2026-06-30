//编译命令：javac PaySystem.java
//运行命令：java PaySystem
import java.util.Scanner;

//1. 支付接口
//抽象类：提起所有支付方式的共同特性
abstract class Payment{
    private String ownerName;  //封装：外部不能直接访问
    private double balance;    //封装：外部不能直接访问
    //2026.6.30 新增：密码与账户锁定逻辑
    private String password;   //密码
    private int failCount = 0; //失败次数
    private boolean isLocked = false; //是否被锁定
    private static final int MAX = 3; //类常变量（大家共享）:允许失败的最大次数

    //01. 构造函数
    Payment(String ownerName, double balance, String password){
        this.ownerName = ownerName;
        this.balance = balance;
        this.password = password;
    }

    //02. 封装：通过 Getter 方法访问私有属性
    public String getOwnerName(){
        return ownerName;
    }
    public double getBalance(){
        return balance;
    }

    //03. 封装：通过方法修改余额，而不是直接操作
    protected void setBalance(double amount){
        balance -= amount;
    }

    //04. 抽象方法：每种支付方式自己实现，只声明"能付款"，不写怎么付
    public abstract String getType();
    public abstract void pay(double amount, String password);

    //05. 公共方法：余额不足的判断逻辑（所有子类通用）
    protected boolean ifEnough(double amount){
        if(amount > balance){
            return false;
        }
        return true;
    }

    //2026.6.30 新增：密码校验，再 pay() 中调用
    protected void checkPassword(String inputPassword){
        if(isLocked){
            throw new AccountLockedException("[支付失败]: 该账户已被锁定,请联系客服解锁...");
        }
        if(!password.equals(inputPassword)){
            failCount++;
            if(failCount >= MAX){
                isLocked = true;
                throw new AccountLockedException("[支付失败]: 密码连续输错 " + MAX + " 次,账户已被锁定...");
            }
            throw new WrongPasswordException("[支付失败]: 密码错误,您还有 " + (MAX - failCount) + " 次尝试机会...");
        }
        //密码正确，重置失败次数
        failCount = 0;
    }
}



//2026.6.30 新增：子类的 pay() 函数需要新增 字符串参数（用户输入的密码）



//2. 子类具体实现：支付宝
class Alipay extends Payment{
    private String AlipayID;  //支付宝的专有属性，只有同类能访问
    //构造方法：初始化对象
    Alipay(String ownerName, String AlipayID, double balance, String password){
        super(ownerName, balance, password);
        this.AlipayID = AlipayID;
    }
   
    @Override
    public String getType(){
        return "支付宝 Alipay";
    }

    //2026.6.30 新增：带密码校验的支付逻辑
    //方法重写
    @Override
    public void pay(double amount, String inputPassword){
        checkPassword(inputPassword);

        if(!ifEnough(amount)){
            System.out.println("[支付失败]: 您的支付宝余额不足...");
            return;
        }
        setBalance(amount);
        System.out.println("[支付成功]: 您已支付 " + amount + " 元");
        System.out.print("您的支付宝ID: " + AlipayID + " 余额还剩 " + getBalance() +" 元");
    }
}

//3. 子类具体实现：微信支付
class WechatPay extends Payment{
    private String phoneNumber; //微信专有属性
    //构造方法：初始化对象
    WechatPay(String ownerName, String phoneNumber, double balance, String password){
        super(ownerName, balance, password);
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getType(){
        return "微信支付 WechatPay";
    }

    @Override
    public void pay(double amount, String inputPassword){
        checkPassword(inputPassword);

        if(!ifEnough(amount)){
            System.out.println("[支付失败]: 您的微信余额不足...");
            return ;
        }
        setBalance(amount);
        System.out.println("[支付成功]: 您已支付 " + amount + " 元");
        System.out.print("您的微信账户: " + phoneNumber + " 余额还剩 " + getBalance() +" 元");
    }
}

//4. 子类具体实现：银行卡
class BankCard extends Payment{
    private String cardNumber;
    private String bankName;
    //构造方法：初始化对象
    BankCard(String ownerName, String bankName, String cardNumber,double balance, String password){
        super(ownerName, balance, password);
        this.bankName = bankName;
        this.cardNumber = cardNumber;
    }

    @Override
    public String getType(){
        return "银行卡支付";
    }

    @Override
    public void pay(double amount, String inputPassword){
        checkPassword(inputPassword);

        if(!ifEnough(amount)){
            System.out.println("[支付失败]: 您的银行卡余额不足...");
            return ;
        }
        setBalance(amount);
        System.out.println("[ " + bankName +" 支付成功]: 您已支付 " + amount + " 元");
        System.out.print("您的银行账户: " + maskCard() + " 余额还剩 " + getBalance() +" 元");
    }

    //银行卡特有：卡号脱敏显示
    private String maskCard(){
        return "**** **** ****" + cardNumber.substring(cardNumber.length() - 4);
    }
}





//自定义异常类：RuntimeException 是 Java 标准库自带的内置类
//1. 用户被锁定异常
class AccountLockedException extends RuntimeException{
    public AccountLockedException(String message){
        super(message);  //把错误信息传给父类存起来,这样 catch 到异常后用 e.getMessage() 就能拿到这条信息
    }
}
//密码错误异常
class WrongPasswordException extends RuntimeException{
    public WrongPasswordException(String message){
        super(message);
    }
}




//程序入口：主函数
public class PaySystem{
    public static void main(String[] args){
        //2026.6.30 新增：让用户可以尝试3次
        boolean paySuccess = false;

        Scanner input = new Scanner(System.in);
        //初始化三种支付方式
        Payment Alipay = new Alipay("花落疏篱", "HuaLuoShuLi", 500.0, "123456");
        Payment WechatPay = new WechatPay("花落疏篱", "13548087179", 1000.0, "Nieshiyi111");
        Payment bankCard = new BankCard("花落疏篱", "招商银行", "1111222233334444", 10000000, "888888");

        Payment[] payMethods = {Alipay, WechatPay, bankCard};   //声明并创建了一个对象数组
        
        System.out.println("====== 支付系统 ======");
        System.out.print("请输入支付金额: ");
        double amount = input.nextDouble();

        System.out.println("\n请选择支付方式:");
        for(int i = 0; i < payMethods.length; i++){
            System.out.println((i + 1) + ". " + payMethods[i].getType());
        }

        System.out.print("请输入选择序号: ");
        int choice = input.nextInt() - 1;
        input.close();

        //2026.6.30 新增：让用户输入支付密码
        while(!paySuccess){
            System.out.print("请输入您的支付密码: ");
            String inputPassword = input.nextLine();
        //用 try-catch 包裹支付操作，捕获密码错误或者账户锁定异常
            try{
                payMethods[choice].pay(amount, inputPassword);
                paySuccess = true;
            }catch(WrongPasswordException sth){     //密码错误，不退出循环
                System.out.println(sth.getMessage());
            }catch(AccountLockedException sth){
                System.out.println(sth.getMessage());
                break;       //账户被异常锁定，强制退出循环，不允许再重试
            }
        }
        input.close();
    }
}