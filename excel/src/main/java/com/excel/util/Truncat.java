package com.excel.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Truncat {
    private ArrayList<Double> dataArrayList;
    private int length;
    private final double alpha = 0.05;

    //传入一组数据，我们要做的是剔除最大或最小的异常值
    public Truncat(ArrayList<Double> arrayList) {
        this.dataArrayList = arrayList;
        this.length = arrayList.size();
    }

    public ArrayList<Double> calc() {
        //因为格拉布斯准则只能对大于等于3个数据进行判断，所以数据量小于3时，直接返回
        if (dataArrayList.size() < 3) {
            return dataArrayList;
        }
        //首先对数据进行排序
        Collections.sort(dataArrayList);
        //求出数据平均值和标准差
        double average = calcAverage(dataArrayList);
        double standard = calcStandard(dataArrayList, length, average);
        // 循环取每个数据和平均数据的标准差，过了就剔除！
        Iterator<Double> it = dataArrayList.iterator();
        while (it.hasNext()) {
            Double item = it.next();
            //与平均值之差
            double diffAvg = (item > average) ? (item - average) : (average - item);
            //差值/标准差
            double waveValue = diffAvg / standard;//波动
            //做比较，是否剔除
            if (waveValue > calcG(alpha, length)) {
                it.remove();
            }
        }
        return dataArrayList;
    }

    //求平均
    public double calcAverage(ArrayList<Double> sample) {
        double sum = 0;
        int cnt = 0;
        for (int i = 0; i < sample.size(); i++) {
            sum += sample.get(i);
            cnt++;
        }

        return (double) sum / cnt;
    }

    //求标准差
    private double calcStandard(ArrayList<Double> array, int n, double average) {
        double sum = 0;
        for (int i = 0; i < n; i++) {
            sum += ((double) array.get(i) - average)
                    * ((double) array.get(i) - average);
        }
        return (double) Math.sqrt((sum / (n - 1)));
    }

    //算临界值的表，这里alpha为0.05
    private double calcG(double alpha, int n) {
        double[] N = {1.1546847100299753, 1.4962499999999703,
                1.763678479497787, 1.9728167175443088, 2.1391059896012203,
                2.2743651271139984, 2.386809875078279, 2.4820832497170997,
                2.564121252001767, 2.6357330437346365, 2.698971864039854,
                2.755372404941574, 2.8061052912205966, 2.8520798130619083,
                2.894013795424427, 2.932482154393285, 2.9679513293748547,
                3.0008041587489247, 3.031358153993366, 3.0598791335206963,
                3.086591582831163, 3.1116865231590722, 3.135327688211162,
                3.157656337622164, 3.178795077984819, 3.198850919445483,
                3.2179177419513314, 3.2360783011390764, 3.2534058719727748,
                3.26996560491852, 3.2858156522011304, 3.301008108808857,
                3.31558980320037, 3.329602965279218, 3.3430857935316243,
                3.356072938839107, 3.368595919061223, 3.3806834758032323,
                3.3923618826659503, 3.403655212591846, 3.41458557057518,
                3.4251732969213213, 3.435437145364717, 3.4453944396432576,
                3.4550612115453876, 3.464452322969104, 3.4735815741386,
                3.482461799798589, 3.491104954935569, 3.4995221913492585,
                3.507723926208097, 3.5157199035634887, 3.5235192496631433,
                3.5311305227901078, 3.5385617582575746, 3.5458205091071684,
                3.5529138829882037, 3.5598485756350797};

        return N[n - 3];
    }

    public static void main(String[] argv) {
        ArrayList<Double> arrayList = new ArrayList<>();
        arrayList.add(12.0);
        arrayList.add(13.0);
        arrayList.add(14.0);
        arrayList.add(15.0);
        arrayList.add(11.0);
        arrayList.add(12.0);
        arrayList.add(13.0);
        arrayList.add(14.0);
        arrayList.add(15.0);
        arrayList.add(12.0);
        arrayList.add(13.0);
        arrayList.add(14.0);
        arrayList.add(15.0);
        arrayList.add(11.0);
        arrayList.add(12.0);
        arrayList.add(13.0);
        arrayList.add(14.0);
        arrayList.add(15.0);
        arrayList.add(12.0);
        arrayList.add(15.0);
        arrayList.add(14.0);
        arrayList.add(42.0);
        arrayList.add(11.0);
        arrayList.add(12.0);
        arrayList.add(13.0);
        arrayList.add(14.0);
        arrayList.add(15.0);
        arrayList.add(78.0);
        arrayList.add(13.0);
        arrayList.add(48.0);

        Truncat g = new Truncat(arrayList);
        ArrayList<Double> calc = g.calc();
        System.out.println(calc.size());

    }
}
