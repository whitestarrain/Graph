import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RailSystem rs = new RailSystem("source/services.txt");
        /*  用于根据输入来进行输出
        Scanner scan=new Scanner(System.in);
        System.out.println("please input two cities:");
        String a=scan.next(),b=scan.next();
        rs.calc_route(a, b);
        rs.output_cheapest_route(a, b); */

        //测试
        rs.calc_route("Paris", "Warsaw");
        rs.output_cheapest_route("Prague", "Skopja");
        rs.calc_route("Paris", "Tirane");
        rs.output_cheapest_route("London", "Tirane");
        rs.calc_route("Belfast", "Dublin");
        rs.calc_route("Paris","Dublin");
        rs.output_cheapest_route("Belfast", "Dublin");
        rs.output_cheapest_route("0", "1");
    }
}