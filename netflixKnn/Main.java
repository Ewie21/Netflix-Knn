import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;


public class Main {
    
    public static void main(String[] args){
        predict(0, getRatings());
    }
    //figure out how to return a hashmap
    public static HashMap<Integer, ArrayList<Integer[]>> getRatings(){
        HashMap<Integer, ArrayList<Integer[]>> muMap = new HashMap<Integer, ArrayList<Integer[]>>();
        File ratingsFile = new File("raings.txt");

        ArrayList<String[]> ratingsArr = new ArrayList<String[]>(); 
        ArrayList<Integer[]> ratingsArrInt = new ArrayList<Integer[]>();
        
        try{
            Scanner reader = new Scanner(ratingsFile);
            while(reader.hasNextLine()){
                String line = reader.nextLine();
                String[] ratingsArrSub = line.split(";");
                ratingsArr.add(ratingsArrSub);
            }
            reader.close();
            //convert to integers
            Integer[] ratingsArrIntSub = new Integer[3];
            for(int i = 0;i<ratingsArr.size();i++){
                for(int j = 0;j<ratingsArr.get(i).length;j++){
                    ratingsArrIntSub[i] = Integer.parseInt(ratingsArr.get(i)[j]);
                    ratingsArrInt.add(ratingsArrIntSub);
                    ratingsArrInt = null;
            }
                if(ratingsArrInt.get(i)[0]== ratingsArrInt.get(i-1)[0]){
                    muMap.put(ratingsArrInt.get(i)[0], ratingsArrInt);
                    ratingsArrInt.clear();
                }
            }


        } catch(FileNotFoundException e){
            System.out.println("This file cannot be found");
            e.printStackTrace();
        }
        return muMap;
    }
    public static Double predict(Integer movieKey, HashMap<Integer, ArrayList<Integer[]>> muMap){   
        ArrayList<Integer[]> indexArrList = muMap.get(movieKey);
        //for(int i = 0; i<muMap.get(movieKey).size();){
        //    indexArrList.add(muMap.get(movieKey)[i]);
        //}
        Double movieScoreSum = 0.00;
        Double count = 0.00;
        for(int i = 0; i<indexArrList.size(); i++){
            movieScoreSum += indexArrList.get(i)[2];
            count++;
        }
        return movieScoreSum/count;

    }
}
