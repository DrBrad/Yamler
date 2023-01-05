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
        //buf = new byte[l.byteSize()];
        put(l);
        return buf;
    }

    public byte[] encode(YamlObject m){
        //buf = new byte[m.byteSize()];
        put(m);
        return buf;
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
    }

    private void put(YamlNumber n){
    }

    private void put(YamlArray l){
    }

    private void put(YamlObject m){
    }

    private List<YamlVariable> decodeArray()throws YamlException {
        ArrayList<YamlVariable> a = new ArrayList<>();

        while(pos < buf.length){
            getDepth();
            if(isList()){
                pos++;
                a.add(getVariable());
            }else{
                break;
            }
            pos++;
        }

        return a;
    }

    public Map<YamlBytes, YamlVariable> decodeObject()throws YamlException {
        ignoreHeader();
        int depth = getDepth();
        pos -= depth;
        return decodeObject(depth);
    }

    private Map<YamlBytes, YamlVariable> decodeObject(int d)throws YamlException {
        HashMap<YamlBytes, YamlVariable> m = new HashMap<>();
        while(pos < buf.length && !isNewLine()){
            if(d != getDepth()){
                throw new YamlException("Depth is incorrect... "+d);
            }

            if(isNewLine()){
                continue;
            }

            if(isFooter()){
                break;
            }

            m.put(getKey(), getVariable());
            pos++;
        }
        return m;
    }

    private YamlBytes getKey(){
        boolean q = false;
        if(buf[pos] == 0x27 || buf[pos] == '"'){
            q = true;
        }

        int s = pos;

        while(pos < buf.length){
            if(buf[pos] == ':'){
                break;
            }
            pos++;
        }

        if(q){
            byte[] b = new byte[pos-s-2];
            System.arraycopy(buf, s+1, b, 0, pos-s-2);
            pos++;

            return new YamlBytes(b);
        }

        byte[] b = new byte[pos-s];
        System.arraycopy(buf, s, b, 0, pos-s);
        pos++;

        return new YamlBytes(b);
    }

    private YamlVariable getVariable()throws YamlException {
        while(pos < buf.length && isSpace()){
            pos++;
        }

        if(isNewLine()){
            pos++;
            return getList();
        }

        if(isMultiLine()){
            pos++;
            return getMultiLine(false);
        }

        if(isMultiLinePreserve()){
            pos++;
            return getMultiLine(true);
        }

        if(buf[pos] == 0x27){
            return getQuote(0x27);
        }

        if(buf[pos] == '"'){
            return getQuote('"');
        }

        int s = pos;
        while(pos < buf.length && !isNewLine()){
            if(isSpace() && buf[pos+1] == '#'){
                break;
            }
            pos++;
        }

        byte[] b = new byte[pos-s];
        System.arraycopy(buf, s, b, 0, pos-s);

        return new YamlBytes(b);
    }

    private YamlVariable getList()throws YamlException {
        while(isNewLine()){
            pos++;
        }

        int d = getDepth();

        if(isList()){
            return new YamlArray(decodeArray());
        }else if(isKey()){
            pos -= d;
            return new YamlObject(decodeObject(d));
        }

        return new YamlBytes(new byte[0]);
    }

    private YamlBytes getQuote(int q){
        pos++;
        int s = pos;
        while(pos < buf.length && buf[pos] != q){
            pos++;
        }

        byte[] b = new byte[pos-s];
        System.arraycopy(buf, s, b, 0, pos-s);
        pos++;

        return new YamlBytes(b);
    }

    private YamlBytes getMultiLine(boolean p){
        while(isNewLine()){
            pos++;
        }

        byte[] b = null;
        getDepthMultiLine();

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

                if(p){
                    b = new byte[c.length+(pos-s)+2];
                    b[c.length] = '\r';
                    b[c.length+1] = '\n';
                    System.arraycopy(c, 0, b, 0, c.length);
                    System.arraycopy(buf, s, b, c.length+2, pos-s);
                }else{
                    b = new byte[c.length+(pos-s)];
                    System.arraycopy(c, 0, b, 0, c.length);
                    System.arraycopy(buf, s, b, c.length, pos-s);
                }
            }
            pos++;

            getDepthMultiLine();

            if(isKey()){
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

    private void ignoreFooter(){
        if(buf[pos] == '.'){
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

    private boolean isComment(){
        return buf[pos] == '#';
    }

    private boolean isFooter(){
        return buf[pos] == '.';
    }

    private boolean isMultiLine(){
        return buf[pos] == '>';
    }

    private boolean isMultiLinePreserve(){
        return buf[pos] == '|';
    }

    private boolean isList(){
        return buf[pos] == '-';
    }

    private boolean isKey(){
        int s = pos;
        while(s < buf.length && !(buf[s] == '\r' || buf[s] == '\n')){
            if(buf[s] == ':'){
                return true;
            }
            s++;
        }
        return false;
    }
}
