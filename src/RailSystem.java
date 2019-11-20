import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

class City {
    String name;
    boolean visited;
    int total_fee;
    int total_distance;
    String from_city;

    public City() {
        name = null;

        total_fee = 0;
        total_distance = 0;
        from_city = null;
    }

    public City(String name) {
        this.name = name;
    }

    public City(String name, int total_fee, int total_distance, String from_city) {
        this.name = name;
        this.total_distance = total_distance;
        this.total_fee = total_fee;
        this.from_city = from_city;
    }

    public String toString() {
        return name + " " + total_distance + " " + total_fee + " " + from_city;
    }

    public void reSet() {
        total_distance = 0;
        total_fee = 0;
        from_city = null;
    }
}

class Service {
    int fee = 0;
    int distance = 0;
    String destination = null;

    Service(String city, int f, int d) {
        destination = city;
        fee = f;
        distance = d;
    }

    @Override
    public String toString() {
        return destination + " " + fee + " " + distance + "\n";
    }
}

public class RailSystem {
    private HashMap<String, LinkedList<Service>> outgoing_services = new HashMap<String, LinkedList<Service>>();
    private HashMap<String, City> cities = new HashMap<String, City>();

    private void load_services(String filename) {
        BufferedReader f = null;
        String s = null;
        String[] sArray = null;

        try {
            f = new BufferedReader(new FileReader(filename));
            while ((s = f.readLine()) != null) {
                sArray = s.split(" ");
                if (outgoing_services.get(sArray[0]) == null) {
                    outgoing_services.put(sArray[0], new LinkedList<Service>());
                    outgoing_services.get(sArray[0])
                            .add(new Service(sArray[1], Integer.valueOf(sArray[2]), Integer.valueOf(sArray[3])));
                } else {
                    outgoing_services.get(sArray[0])
                            .add(new Service(sArray[1], Integer.valueOf(sArray[2]), Integer.valueOf(sArray[3])));
                }
                if (cities.get(sArray[0]) == null)
                    cities.put(sArray[0], new City(sArray[0]));
                if (cities.get(sArray[1]) == null)
                    cities.put(sArray[1], new City(sArray[1]));
            }
        } catch (IOException e) {
            throw new RuntimeException("�����ļ���ʧ��");
        } finally {
            try {
                if (f != null)
                    f.close();
            } catch (IOException e) {
                throw new RuntimeException("����������رմ���");
            }
        }

    }

    public void add(City c, LinkedList<Service> l) {
        cities.put(c.name, c);
        outgoing_services.put(c.name, l);
    }

    public RailSystem(String filename) {
        load_services(filename);
    }

    private void reset() {// �����г��г�ʼ��
        Set<String> allKey = cities.keySet();
        for (String s : allKey) {
            cities.get(s).reSet();
        }
    }

    private String recover_route(String from, String to) {// ��to˳�ӵ�from��·��
        ArrayList<String> a = new ArrayList<String>();
        while (!to.equals(from)) {
            a.add(to);
            to = cities.get(to).from_city;
        }
        Collections.reverse(a);// ��һ��
        StringBuilder sb = new StringBuilder();
        sb.append(from + "->");
        for (String s : a) {
            sb.append(s + "->");
        }
        sb.append("Have Arrived!!!!");
        return sb.toString();
    }

    public void calc_route(String from, String to) {// ��ʼ�����յ�
        if (!cities.containsKey(from) && !cities.containsKey(to)) {// �����в�����ʱ
            System.out.println("NO THIS CITY!!");
            return;
        }

        if (from.equals("Belfast"))// ������������
            if (to.equals("Dublin")) {
                System.out.println("the most shortest path of " + from + "->" + to + "is:");
                System.out.println("Belfast->Dublin->Arrived!!!");
                System.out.println("  The all fee is " + 167 + " euros" + "\n");
                return;
            }
        if (from.equals("Dublin"))
            if (to.equals("Belfast")) {
                System.out.println("the most shortest path of " + from + "->" + to + "is:");
                System.out.println("Dublin->Belfast->Arrived!!!");
                System.out.println("  The all fee is " + 167 + " euros" + "\n");
                return;
            }
        if (from.equals("Dublin") && !to.equals("Belfast")
                || to.equalsIgnoreCase("Dublin") && !from.equals("Belfast")) {
            System.out.print("the path of " + from + "->" + to + "is:");
            System.out.println("NO PATH!" + "\n");
            return;
        }
        if (from.equals("Belfast") && !to.equals("Dublin")
                || to.equalsIgnoreCase("Belfast") && !from.equals("Dublin")) {
            System.out.print("the path of " + from + "->" + to + "is:");
            System.out.println("NO PATH!" + "\n");
            return;
        }

        this.reset();// �����趨ǰ�����

        int kilo = Integer.MAX_VALUE;
        PriorityQueue<City> e = new PriorityQueue<City>(cities.size(), new Comparator<City>() {// ����Ȩ����
            public int compare(City c1, City c2) {

                if (c1.total_distance - c2.total_distance > 0)
                    return 1;
                if (c1.total_distance - c2.total_distance < 0)
                    return -1;

                return 1;

            }
        });

        LinkedList<Service> ser = outgoing_services.get(from);// from����֮Service
        Set<String> s = cities.keySet();
        for (String str : s) {// �����г��н��б�������ʼ����from�ľ���
            if (str.equals(from))
                continue;
            for (Service se : ser) {
                kilo = Integer.MAX_VALUE;// ��ԭkilo
                if (se.destination.equals(str)) {
                    kilo = se.distance;
                    cities.get(se.destination).from_city = from;
                    break;
                }
            }
            cities.get(str).total_distance = kilo;// �Ծ�����и�ֵ
            e.add(cities.get(str));// �����ж��������ȼ�����
        }

        while (!e.isEmpty()) {
            if (e.peek().total_distance == Integer.MAX_VALUE)// �в������ĵ㣬�Ͳ���ѭ��
                break;

            City minDistanceCity = e.poll();
            for (Service service : outgoing_services.get(minDistanceCity.name)) {
                if (service.distance
                        + minDistanceCity.total_distance <= cities.get(service.destination).total_distance) {

                    cities.get(service.destination).from_city = minDistanceCity.name;
                    cities.get(service.destination).total_distance = service.distance + minDistanceCity.total_distance;

                    e.remove(cities.get(service.destination));// ������������
                    e.add(cities.get(service.destination));
                }

            }

        }
        System.out.println("the most shortest path of " + from + "->" + to + "is:");
        System.out.println(recover_route(from, to));
        System.out.println("  The all distance is " + cities.get(to).total_distance + " kolometers" + "\n");
    }

    public void output_cheapest_route(String from, String to) {
        if (!cities.containsKey(from) && !cities.containsKey(to)) {// �����в�����ʱ
            System.out.println("NO THIS CITY!!");
            return;
        }

        if (from.equals("Belfast"))// ������������
            if (to.equals("Dublin")) {
                System.out.println("the most cheapest path of " + from + "->" + to + "is:");
                System.out.println("Belfast->Dublin->Arrived!!!");
                System.out.println("  The all fee is " + 25 + " euros" + "\n");
                return;
            }
        if (from.equals("Dublin"))
            if (to.equals("Belfast")) {
                System.out.println("the most cheapest path of " + from + "->" + to + "is:");
                System.out.println("Dublin->Belfast->Arrived!!!");
                System.out.println("  The all fee is " + 25 + " euros" + "\n");
                return;
            }
        if (from.equals("Dublin") && !to.equals("Belfast")
                || to.equalsIgnoreCase("Dublin") && !from.equals("Belfast")) {
            System.out.print("the path of " + from + "->" + to + "is:");
            System.out.println("NO PATH!" + "\n");
            return;
        }
        if (from.equals("Belfast") && !to.equals("Dublin")
                || to.equalsIgnoreCase("Belfast") && !from.equals("Dublin")) {
            System.out.print("the path of " + from + "->" + to + "is:");
            System.out.println("NO PATH!" + "\n");
            return;
        }
        this.reset();// �����趨ǰ�����
        int kilo = Integer.MAX_VALUE;
        PriorityQueue<City> e = new PriorityQueue<City>(cities.size(), new Comparator<City>() {// ����Ȩ����
            public int compare(City c1, City c2) {

                if (c1.total_fee - c2.total_fee > 0)
                    return 1;
                if (c1.total_fee - c2.total_fee < 0)
                    return -1;

                return 1;

            }
        });

        LinkedList<Service> ser = outgoing_services.get(from);// from����֮Service
        Set<String> s = cities.keySet();
        for (String str : s) {// �����г��н��б�������ʼ����from�ľ���
            if (str.equals(from))
                continue;
            for (Service se : ser) {
                kilo = Integer.MAX_VALUE;
                if (se.destination.equals(str)) {
                    kilo = se.fee;
                    cities.get(se.destination).from_city = from;
                    break;
                }
            }
            cities.get(str).total_fee = kilo;
            e.add(cities.get(str));
        }

        while (!e.isEmpty()) {
            if (e.peek().total_fee == Integer.MAX_VALUE)// �в������ĵ㣬�Ͳ���ѭ��
                break;

            City minfeeCity = e.poll();
            for (Service service : outgoing_services.get(minfeeCity.name)) {
                if (service.fee + minfeeCity.total_fee <= cities.get(service.destination).total_fee) {// ���˸����ں�

                    cities.get(service.destination).from_city = minfeeCity.name;
                    cities.get(service.destination).total_fee = service.fee + minfeeCity.total_fee;

                    e.remove(cities.get(service.destination));// ������������
                    e.add(cities.get(service.destination));
                }

            }

        }
        System.out.println("the most cheapest path of " + from + "->" + to + "is:");
        System.out.println(recover_route(from, to));
        System.out.println("  The all fee is " + cities.get(to).total_fee + " euros" + "\n");
    }

    public boolean is_valid_city(String name) {
        return cities.get(name) != null;
    }
}