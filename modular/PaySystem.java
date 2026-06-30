package modular;
//编译命令：javac *.java
//运行命令：java PaySystem
import java.util.Scanner;

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

        //2026.6.30新增：nextInt() nextDouble() 和 nextLine() 混用时要注意[换行符]的问题！！
        input.nextLine();   //用处： 吃掉nextInt()回车以后残留下来的换行符，避免被下一个 nextLine() 读到导致程序以为用户第一次输入的密码是一个空白行

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