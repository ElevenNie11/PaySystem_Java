import java.util.Scanner;
//1. 支付接口
//抽象类：提起所有支付方式的共同特性
abstract class Payment{
    private String ownerName;  //封装：外部不能直接访问
    private double balance;    //封装：外部不能直接访问

    //01. 构造函数
    Payment(String ownerName, double balance){
        this.ownerName = ownerName;
        this.balance = balance;
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
    public abstract void pay(double amount);

    //05. 公共方法：余额不足的判断逻辑（所有子类通用）
    protected boolean ifEnough(double amount){
        if(amount > balance){
            return false;
        }
        return true;
    }
}

//2. 子类具体实现：支付宝
class Alipay extends Payment{
    private String AlipayID;  //支付宝的专有属性，只有同类能访问
    //构造方法：初始化对象
    Alipay(String ownerName, String AlipayID, double balance){
        super(ownerName, balance);
        this.AlipayID = AlipayID;
    }
   
    @Override
    public String getType(){
        return "支付宝 Alipay";
    }


    @Override      //方法重写s
    public void pay(double amount){
        if(!ifEnough(amount)){
            System.out.println("[支付失败]: 您的支付宝余额不足...");
            return ;
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
    WechatPay(String ownerName, String phoneNumber, double balance){
        super(ownerName, balance);
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getType(){
        return "微信支付 WechatPay";
    }

    @Override
    public void pay(double amount){
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
    BankCard(String ownerName, String bankName, String cardNumber,double balance){
        super(ownerName, balance);
        this.bankName = bankName;
        this.cardNumber = cardNumber;
    }

    @Override
    public String getType(){
        return "银行卡支付";
    }

    @Override
    public void pay(double amount){
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

public class PaySystem{
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        //初始化三种支付方式
        Payment Alipay = new Alipay("花海", "FlowerSea", 500.0);
        Payment WechatPay = new WechatPay("花海", "13548087179", 1000.0);
        Payment bankCard = new BankCard("花海", "招商银行", "1111222233334444", 10000000);

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

        payMethods[choice].pay(amount);
    }
}