public class Tag{

    private String t;
    private String f;

    public Tag(String a, String b){
        t = a;
        f = b;
    }

    public void setT(String a){
        t = a;
    }

    public void setF(String a){
        f = a;
    }

    public String getT(){
        return t;
    }

    public String getF(){
        return f;
    }

    public String toString(){
        return "t: " + t + "\tf: " + f;
    }
}