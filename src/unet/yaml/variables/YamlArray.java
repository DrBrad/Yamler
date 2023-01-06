package unet.yaml.variables;

import unet.yaml.YamlException;
import unet.yaml.Yamler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class YamlArray implements YamlVariable {

    private ArrayList<YamlVariable> l = new ArrayList<>();

    public YamlArray(){
    }

    public YamlArray(List<?> l){
        for(Object v : l){
            if(v instanceof YamlVariable){
                add((YamlVariable) v);
            }else if(v instanceof Number){
                add((Number) v);
            }else if(v instanceof String){
                add((String) v);
            }else if(v instanceof byte[]){
                add((byte[]) v);
            }else if(v instanceof List<?>){
                add((List<?>) v);
            }else if(v instanceof Map<?, ?>){
                add((Map<?, ?>) v);
            }
        }
    }

    private void add(YamlVariable v){
        l.add(v);
    }

    public void add(Number n){
        add(new YamlNumber(n.toString()));
    }

    public void add(byte[] b){
        add(new YamlBytes(b));
    }

    public void add(String s){
        add(new YamlBytes(s.getBytes()));
    }

    public void add(List<?> l){
        add(new YamlArray(l));
    }

    public void add(Map<?, ?> l){
        add(new YamlObject(l));
    }

    public void add(YamlArray a){
        l.add(a);
    }

    public void add(YamlObject o){
        l.add(o);
    }

    private void set(int i, YamlVariable v){
        l.set(i, v);
    }

    public void set(int i, Number n){
        set(i, new YamlNumber(n.toString()));
    }

    public void set(int i, byte[] b){
        set(i, new YamlBytes(b));
    }

    public void set(int i, String s){
        set(i, new YamlBytes(s.getBytes()));
    }

    public void set(int i, List<?> l){
        set(i, new YamlArray(l));
    }

    public void set(int i, Map<?, ?> m){
        set(i, new YamlObject(m));
    }

    public void set(int i, YamlArray a){
        l.set(i, a);
    }

    public void set(int i, YamlObject o){
        l.set(i, o);
    }

    public YamlVariable valueOf(int i){
        return l.get(i);
    }

    public Object get(int i){
        return l.get(i).getObject();
    }

    public Integer getInteger(int i){
        return ((Number) l.get(i).getObject()).intValue();
    }

    public Long getLong(int i){
        return ((Number) l.get(i).getObject()).longValue();
    }

    public Short getShort(int i){
        return ((Number) l.get(i).getObject()).shortValue();
    }

    public Double getDouble(int i){
        return ((Number) l.get(i).getObject()).doubleValue();
    }

    public Float getFloat(int i){
        return ((Number) l.get(i).getObject()).floatValue();
    }

    public String getString(int i){
        return new String((byte[]) l.get(i).getObject());
    }

    public byte[] getBytes(int i){
        return (byte[]) l.get(i).getObject();
    }

    public YamlArray getYamlArray(int i){
        return (YamlArray) l.get(i);
    }

    public YamlObject getYamlObject(int i){
        return (YamlObject) l.get(i);
    }

    public boolean contains(Number n){
        return l.contains(new YamlNumber(n.toString()));
    }

    public boolean contains(String s){
        return l.contains(new YamlBytes(s.getBytes()));
    }

    public boolean contains(byte[] b){
        return l.contains(new YamlBytes(b));
    }

    public boolean contains(List<?> l){
        return this.l.contains(new YamlArray(l));
    }

    public boolean contains(Map<?, ?> m){
        return l.contains(new YamlObject(m));
    }

    public boolean contains(YamlArray a){
        return l.contains(a);
    }

    public boolean contains(YamlObject o){
        return l.contains(o);
    }

    private void remove(YamlVariable v){
        if(l.contains(v)){
            l.remove(v);
        }
    }

    public void remove(Number n){
        remove(new YamlNumber(n.toString()));
    }

    public void remove(byte[] b){
        remove(new YamlBytes(b));
    }

    public void remove(String s){
        remove(new YamlBytes(s.getBytes()));
    }

    private int indexOf(YamlVariable v){
        if(l.contains(v)){
            return l.indexOf(v);
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public int indexOf(Number n){
        return indexOf(new YamlNumber(n.toString()));
    }

    public int indexOf(byte[] b){
        return indexOf(new YamlBytes(b));
    }

    public int indexOf(String s){
        return indexOf(new YamlBytes(s.getBytes()));
    }

    public int size(){
        return l.size();
    }

    @Override
    public Object getObject(){
        ArrayList<Object> a = new ArrayList<>();
        for(YamlVariable v : l){
            a.add(v.getObject());
        }
        return a;
    }

    @Override
    public int hashCode(){
        return 2;
    }

    @Override
    public String toString(){
        StringBuilder b = new StringBuilder("[\r\n");

        for(YamlVariable v : l){
            if(v instanceof YamlNumber){
                b.append("\t\033[0;31m"+v.getObject()+"\033[0m\r\n");

            }else if(v instanceof YamlBytes){
                if(Charset.forName("US-ASCII").newEncoder().canEncode(new String((byte[]) v.getObject()))){
                    b.append("\t\033[0;34m"+new String((byte[]) v.getObject(), StandardCharsets.UTF_8)+"\033[0m\r\n");

                }else{
                    b.append("\t\033[0;34mBASE64 { "+ Base64.getEncoder().encodeToString((byte[]) v.getObject())+" }\033[0m\r\n");
                }

            }else if(v instanceof YamlArray){
                b.append("\t\033[0m"+((YamlArray) v).toString().replaceAll("\\r?\\n", "\r\n\t")+"\r\n");

            }else if(v instanceof YamlObject){
                b.append("\t\033[0m"+((YamlObject) v).toString().replaceAll("\\r?\\n", "\r\n\t")+"\r\n");
            }
        }

        return b+"]";
    }
}
