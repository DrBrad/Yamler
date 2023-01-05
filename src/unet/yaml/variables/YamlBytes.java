package unet.yaml.variables;

import java.util.Arrays;

public class YamlBytes implements YamlVariable {

    private byte[] b;

    public YamlBytes(byte[] b){
        this.b = b;
    }

    public byte[] getBytes(){
        /*
        byte[] r = new byte[s];
        byte[] l = (b.length+":").getBytes();
        System.arraycopy(l, 0, r, 0, l.length);
        System.arraycopy(b, 0, r, l.length, b.length);
        */
        return b;
    }

    @Override
    public byte[] getObject(){
        return b;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof YamlBytes){
            return Arrays.equals(getBytes(), ((YamlBytes) o).getBytes());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return 0;
    }
}
