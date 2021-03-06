package com.pervasif2014.kelompok5.sensory;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class KNN {

    private DataSource Ds;
    private Instances Data;
    private Classifier ibk;

    public void init(InputStream is) throws Exception
    {
        try
        {
            Ds = new DataSource(is);
        }
        catch (Exception e)
        {
            System.out.println("cannot open file");
            e.printStackTrace();
        }
        try
        {
            Data = Ds.getDataSet();
        }
        catch (Exception e)
        {
            System.out.println("error type");
        }

        if (Data.classIndex() == -1)
            Data.setClassIndex(Data.numAttributes() - 1);


        //do not use first and second

        ibk = new IBk();
        ibk.buildClassifier(Data);
    }

    public static BufferedReader readDataFile(String filename) {
        BufferedReader inputReader = null;

        try {
            inputReader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + filename);
        }

        return inputReader;
    }

    public double Classify(double Ax , double Ay , double Az , double Gx ,
                           double Gy , double Gz , double Lx ,  double Ly , double Lz) throws Exception {
        Instance test = new Instance(10);
        test.setDataset(Data);
        test.setValue(0, Ax);
        test.setValue(1, Ay);
        test.setValue(2, Az);
        test.setValue(3, Gx);
        test.setValue(4, Gy);
        test.setValue(5, Gz);
        test.setValue(6, Lx);
        test.setValue(7, Ly);
        test.setValue(8, Lz);
        test.setValue(9, 0.0);


        double kelas = ibk.classifyInstance(test);

        return kelas;

    }

}

