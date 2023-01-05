package unet.yaml.variables;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;

public class YamlNumber implements YamlVariable {

    private String n;

    public YamlNumber(String n){
        this.n = n;
    }

    public byte[] getBytes(){
        return n.getBytes();
    }

    @Override
    public Number getObject(){
        try{
            return NumberFormat.getInstance().parse(n);
        }catch(ParseException e){
            return 0;
        }
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof YamlNumber){
            return Arrays.equals(getBytes(), ((YamlNumber) o).getBytes());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return 1;
    }
}
