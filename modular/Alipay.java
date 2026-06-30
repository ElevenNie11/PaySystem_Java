package modular;
//2. 子类具体实现：支付宝
public class Alipay extends Payment{
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