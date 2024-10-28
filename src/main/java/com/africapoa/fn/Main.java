package com.africapoa.fn;

import com.africapoa.fn.ds.JsonQ_Old;

import java.io.File;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        JsonQ_Old jsonQ= JsonQ_Old.fromIO(new File("/home/n2/Downloads/sample.json"));
        List<String> val=jsonQ.get("..code").getStrings("[-1,0]");
        System.out.println(val);


    }
}