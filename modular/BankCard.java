package modular;
//4. 子类具体实现：银行卡
public class BankCard extends Payment{
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