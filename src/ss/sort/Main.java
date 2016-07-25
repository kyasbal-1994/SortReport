package ss.sort;
import java.io.*;

interface Delegate<T, R> {
    public R invoke(T arg);
}

class ScoreElement{
    public int id;

    public String rika;

    public int sum;

    public ScoreElement next;

    public ScoreElement prev;

    public long getCriteria(){
        return (long)sum * Integer.MAX_VALUE + (Integer.MAX_VALUE-id);
    }

    public  ScoreElement(int id,String rika,int sum){
        this.id = id;
        this.rika  = rika;
        this.sum = sum;
    }

    public void insert(ScoreElement elem){
        if(this.getCriteria() < elem.getCriteria())
        {
            if(next == null)next = elem;
            else {
                if(elem.getCriteria() < next.getCriteria())
                {
                    elem.insert(next);
                    next = elem;
                }else{
                    next.insert(elem);
                }
            }
        }else{
            if(prev == null)prev = elem;
            else{
                if(elem.getCriteria() > prev.getCriteria())
                {
                    elem.insert(prev);
                    prev = elem;
                }else{
                    prev.insert(elem);
                }
            }
        }
    }

    public void enumerate(Delegate<ScoreElement,Boolean> handler){
        this.enumerate(handler,this);
    }

    private boolean enumerate(Delegate<ScoreElement,Boolean> handler,ScoreElement current){
        if(current.next!=null && !enumerate(handler,current.next)){
            return false;
        }
        if(!handler.invoke(current)){
            return false;
        }
        if(current.prev!=null && !enumerate(handler,current.prev)){
            return false;
        }
        return true;
    }

}

public class Main {

    private static String[] scienceLabels = {"物理B","化学B","生物B","地学B"};

    private static ScoreElement readNext(BufferedReader fr) throws IOException
    {
        String line = fr.readLine();
        if(line == null)return null;
        String[] splitted = line.split("\\s+");
        if(splitted.length != 9)return null;
        int[] scores = new int[8];
        for(int i = 1; i < splitted.length; i++)
        {
            scores[i-1] = Integer.parseInt(splitted[i].trim());
        }
        String rikaLabel = "";
        int sum = scores[1] + scores[2] + scores[3];
        for (int i = 0; i < 2; i++) {
            int max = 0;
            int index = 0;
            for (int j = 0; j < 4; j++) {
                if(max < scores[j+4]) {
                    index = j;
                    max = scores[j + 4];
                }
            }
            sum += max;
            scores[index + 4] = 0;
            rikaLabel += scienceLabels[index];
        }
        return new ScoreElement(scores[0],rikaLabel,sum);
    }

    public static void main(String[] args) throws Exception{
        FileReader fr = new FileReader("Seiseki.dat");
        BufferedReader br = new BufferedReader(fr);
        ScoreElement firstElem = readNext(br);
        ScoreElement s;
        while((s = readNext(br))!=null)
        {
            firstElem.insert(s);
        }

        firstElem.enumerate(new Delegate<ScoreElement, Boolean>() {

            private int lastScore = -1;

            private int lastRanking = 0;

            private int sameScoreCount  = 1;

            @Override
            public Boolean invoke(ScoreElement arg) {
                if(arg.sum != lastScore){
                    lastRanking += sameScoreCount;
                    sameScoreCount = 1;
                    lastScore = arg.sum;
                }else{
                    sameScoreCount ++;
                }
                if(lastRanking <=300) {
                    System.out.printf("%d %d %d %s\n", lastRanking, arg.id, arg.sum, arg.rika);
                    return true;
                }else{
                    return false;
                }
            }
        });
    }
}
