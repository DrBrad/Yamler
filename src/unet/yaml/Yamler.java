package unet.yaml;

import unet.yaml.variables.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Yamler {

    private byte[] buf;
    private int pos = 0;

    public byte[] encode(YamlArray l){
        return null;
    }

    public byte[] encode(YamlObject m){
        return null;
    }

    public List<YamlVariable> decodeArray(byte[] buf, int off){
        this.buf = buf;
        pos = off;
        return decodeArray();
    }

    public Map<YamlBytes, YamlVariable> decodeObject(byte[] buf, int off)throws YamlException {
        this.buf = buf;
        pos = off;
        return decodeObject();
    }
    
    private void put(YamlVariable v){
        if(v instanceof YamlBytes){
            put((YamlBytes) v);
        }else if(v instanceof YamlNumber){
            put((YamlNumber) v);
        }else if(v instanceof YamlArray){
            put((YamlArray) v);
        }else if(v instanceof YamlObject){
            put((YamlObject) v);
        }
    }

    private void put(YamlBytes v){
        byte[] b = v.getBytes();
        System.arraycopy(b, 0, buf, pos, b.length);
        pos += b.length;
    }

    private void put(YamlNumber n){
        byte[] b = n.getBytes();
        System.arraycopy(b, 0, buf, pos, b.length);
        pos += b.length;
    }

    private void put(YamlArray l){
        buf[pos] = 'l';
        pos++;

        for(int i = 0; i < l.size(); i++){
            put(l.valueOf(i));
        }
        buf[pos] = 'e';
        pos++;
    }

    private void put(YamlObject m){
        buf[pos] = 'd';
        pos++;

        for(YamlBytes k : m.keySet()){
            put(k);
            put(m.valueOf(k));
        }
        buf[pos] = 'e';
        pos++;
    }

    private List<YamlVariable> decodeArray(){
        /*
        if(buf[pos] == 'l'){
            ArrayList<YamlVariable> a = new ArrayList<>();
            pos++;

            while(buf[pos] != 'e'){
                a.add(get());
            }
            pos++;
            return a;
        }
        */
        return null;
    }

    public Map<YamlBytes, YamlVariable> decodeObject()throws YamlException {
        ignoreHeader();
        int depth = getDepth();
        pos -= depth;
        return decodeObject(depth);
    }

    public Map<YamlBytes, YamlVariable> decodeObject(int d)throws YamlException {
        HashMap<YamlBytes, YamlVariable> m = new HashMap<>();
        while(pos < buf.length){
            if(!isNewLine()){
                if(d != getDepth()){
                    throw new YamlException("Depth is incorrect... "+d);
                }
                //System.out.println((char)buf[pos]);
                //System.out.println(new String(buf, pos-5, pos));
                if(isNewLine()){
                    continue;
                }

                m.put(getKey(), getVariable(d));
            }
            pos++;
        }
        return m;
    }

    private YamlBytes getKey(){
        int s = pos;

        while(pos < buf.length){
            if(buf[pos] == ':'){
                break;
            }
            pos++;
        }

        byte[] b = new byte[pos-s];
        System.arraycopy(buf, s, b, 0, pos-s);
        pos++;

        return new YamlBytes(b);
    }

    private YamlVariable getVariable(int d)throws YamlException {
        while(pos < buf.length && isSpace()){
            pos++;
        }

        if(isNewLine()){
            pos++;
            return getList(d);
        }

        if(isMultiLine()){
            pos++;
            return getMultiLine(d);
        }

        int s = pos;
        while(pos < buf.length && !isNewLine()){
            pos++;
        }

        byte[] b = new byte[pos-s];
        System.arraycopy(buf, s, b, 0, pos-s);

        return new YamlBytes(b);
    }

    private YamlVariable getList(int d){
        System.out.println("IS LIST OR MAP???");
        while(isNewLine()){
            pos++;
        }

        int de = getDepth();

        if(de > d){

        }

        return new YamlBytes("asd".getBytes());
    }

    private YamlBytes getMultiLine(int d)throws YamlException {
        while(isNewLine()){
            pos++;
        }

        byte[] b = null;
        int de = getDepthMultiLine();

        if(de <= d){
            throw new YamlException("Depth error with multiline string.");
        }

        while(pos < buf.length){
            int s = pos;
            while(pos < buf.length && !isNewLine()){
                pos++;
            }

            if(b == null){
                b = new byte[pos-s];
                System.arraycopy(buf, s, b, 0, pos-s);

            }else{
                byte[] c = new byte[b.length];
                System.arraycopy(b, 0, c, 0, b.length);

                b = new byte[c.length+(pos-s)+2];
                b[c.length] = '\r';
                b[c.length+1] = '\n';
                System.arraycopy(c, 0, b, 0, c.length);
                System.arraycopy(buf, s, b, c.length+2, pos-s);
            }
            pos++;

            if(de != getDepthMultiLine()){
                break;
            }
        }
        pos--;

        return new YamlBytes(b);
    }

    private void ignoreHeader(){
        if(buf[pos] == '-'){
            while(pos < buf.length && buf[pos] != '\n'){
                pos++;
            }
            pos++;
        }
    }

    private void ignoreComment(){
        while(pos < buf.length && !isNewLine()){
            pos++;
        }
        pos++;
    }

    private int getDepth(){
        int s = pos;
        while(pos < buf.length && isSpace()){
            pos++;
        }

        if(isComment()){
            ignoreComment();
            return getDepth();
        }

        return pos-s;
    }

    private int getDepthMultiLine(){
        int s = pos;
        while(pos < buf.length && isSpace()){
            pos++;
        }

        if(pos-s == 0){
            if(isComment()){
                ignoreComment();
                return getDepth();
            }
        }

        return pos-s;
    }

    private boolean isSpace(){
        return (buf[pos] == 0x20 || buf[pos] == '\t');
    }

    private boolean isNewLine(){
        return (buf[pos] == '\r' || buf[pos] == '\n');
    }

    public boolean isComment(){
        return buf[pos] == '#';
    }

    public boolean isMultiLine(){
        return buf[pos] == '|';
    }
}
