package unet.yaml.variables;

import java.util.Arrays;

public class YamlBytes implements YamlVariable {

    private byte[] b;

    public YamlBytes(byte[] b){
        for(int i = 0; i < b.length; i++){
            if(b[i] == '\r' || b[i] == '\n'){
                this.b = new byte[b.length+2];
                System.arraycopy(b, 0, this.b, 1, b.length);
                this.b[0] = '"';
                this.b[this.b.length-1] = '"';
                return;
            }
        }

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
    public int byteSize(){
        return b.length;
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
