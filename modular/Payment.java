package modular;
//1. 支付接口
//抽象类：提起所有支付方式的共同特性
public abstract class Payment{
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