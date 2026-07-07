package modular;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

//3. 子类具体实现：微信支付
public class WechatPay extends Payment{
    private String phoneNumber; //微信专有属性
    private static final String FilePath = "wechat_balance.txt";  //2026.7.7新增：数据持久化
    //构造方法：初始化对象
    WechatPay(String ownerName, String phoneNumber, double balance, String password){
        super(ownerName, loadBalance(balance), password);         //2206.7.7新增
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
        saveBalance();     // 2026.7.7 新增
        System.out.println("[支付成功]: 您已支付 " + amount + " 元");
        System.out.print("您的微信账户: " + phoneNumber + " 余额还剩 " + getBalance() +" 元");
    }

    //2026.7.7 新增：从文件里读取余额
    private static double loadBalance(double defaultBalance){
        File file = new File(FilePath);
        if(!file.exists()){
            writeBalance(defaultBalance);
            return defaultBalance;
        }
        try(Scanner fileReader = new Scanner(file)){
            if(fileReader.hasNextDouble()){         //先检查：下一个 token 是合法的 double 类型
                return fileReader.nextDouble();
            }
        }catch(FileNotFoundException sth){
            System.out.println("读取余额文件失败,使用默认余额");
        }
        return defaultBalance;
    }
    //2206.7.7 新增：保存余额
    private void saveBalance(){
        writeBalance(getBalance());
    }
    //2026.7.7 新增：实际执行写文件操作的静态工具方法
    private static void writeBalance(double balance){
        try(PrintWriter output = new PrintWriter(new FileWriter(FilePath));){
            output.print(balance);
        }catch(IOException sth){
            System.out.println("保存余额失败: " + sth.getMessage());
        }
    }
}