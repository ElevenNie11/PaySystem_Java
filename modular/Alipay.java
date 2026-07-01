package modular;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

//2. 子类具体实现：支付宝
public class Alipay extends Payment{
    private String AlipayID;  //支付宝的专有属性，只有同类能访问
    private static final String FilePath = "alipay_balance.txt";  //2026.7.1 新增：持久化数据存放的文件路径（类似 C语言中的宏定义）
    //构造方法：初始化对象
    Alipay(String ownerName, String AlipayID, double balance, String password){
        super(ownerName, loadBalance(balance), password);        //2026.7.1 新增：尝试从文件读取余额
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
        saveBalance();      //2026.7.1 新增：扣款成功以后，把最新余额写进文件
        System.out.println("[支付成功]: 您已支付 " + amount + " 元");
        System.out.print("您的支付宝ID: " + AlipayID + " 余额还剩 " + getBalance() +" 元");
    }

    //2026.7.1 新增：从文件里读取余额，文件不存在则返回 PaySystem.java 里设置的默认值同时创建文件
    private static double loadBalance(double defaultBalance){
        File file = new File(FilePath);        //这保存着文件的各种属性
        if(!file.exists()){
            //第一次运行文件不存在，直接写入默认值
            writeBalance(defaultBalance);
            return defaultBalance;
        }
        //文件存在，则读取里面的余额
        // try-with-resources
        // Scanner 类用于读取输入源的数据（输入源包括：键盘/文件等）
        try(Scanner fileReader = new Scanner(file)){
            if(fileReader.hasNextDouble()){         //先检查：下一个 token 是合法的 double 类型
                return fileReader.nextDouble();
            }
        }catch(FileNotFoundException sth){          //理论上不会走到这里,因为前面已经判断过 file.exists()
            System.out.println("读取余额文件失败,使用默认余额");
        }
        return defaultBalance;
    }

    //2026.7.1 新增：把当前余额写进文件（调用 getBalance() 拿到当前最新值）
    private void saveBalance(){
        writeBalance(getBalance());
    }

    //2026.7.1 新增：实际执行写文件操作的静态工具方法
    private static void writeBalance(double balance){
        try(PrintWriter output = new PrintWriter(new FileWriter(FilePath));){
            output.print(balance);
        }catch(IOException sth){
            System.out.println("保存余额失败: " + sth.getMessage());
        }
    } 
}