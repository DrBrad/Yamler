package unet.yaml.variables;

import unet.yaml.YamlException;
import unet.yaml.Yamler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class YamlObject implements YamlVariable {

    private HashMap<YamlBytes, YamlVariable> m = new HashMap<>();

    public YamlObject(){
    }

    public YamlObject(Map<?, ?> m){
        for(Object o : m.keySet()){
            YamlBytes k;

            if(o instanceof YamlBytes){
                k = (YamlBytes) o;
            }else if(o instanceof String){
                k = new YamlBytes(((String) o).getBytes());
            }else{
                throw new IllegalArgumentException("Map keys must be in byte, string, or Yamlbyte form.");
            }

            if(m.get(o) instanceof YamlVariable){
                put(k, (YamlVariable) m.get(o));
            }else if(m.get(o) instanceof Number){
                put(k, (Number) m.get(o));
            }else if(m.get(o) instanceof String){
                put(k, (String) m.get(o));
            }else if(m.get(o) instanceof byte[]){
                put(k, (byte[]) m.get(o));
            }else if(m.get(o) instanceof List<?>){
                put(k, (List<?>) m.get(o));
            }else if(m.get(o) instanceof Map<?, ?>){
                put(k, (Map<?, ?>) m.get(o));
            }
        }
    }

    public YamlObject(byte[] buf)throws YamlException {
        this(new Yamler().decodeObject(buf, 0));
    }

    public YamlObject(byte[] buf, int off)throws YamlException {
        this(new Yamler().decodeObject(buf, off));
    }

    private void put(YamlBytes k, YamlVariable v){
        m.put(k, v);
    }

    private void put(YamlBytes k, Number n){
        put(k, new YamlNumber(n.toString()));
    }

    private void put(YamlBytes k, byte[] b){
        put(k, new YamlBytes(b));
    }

    private void put(YamlBytes k, String s){
        put(k, new YamlBytes(s.getBytes()));
    }

    private void put(YamlBytes k, List<?> l){
        put(k, new YamlArray(l));
    }

    private void put(YamlBytes k, Map<?, ?> l){
        put(k, new YamlObject(l));
    }

    public void put(String k, Number n){
        put(new YamlBytes(k.getBytes()), new YamlNumber(n.toString()));
    }

    public void put(String k, byte[] b){
        put(new YamlBytes(k.getBytes()), new YamlBytes(b));
    }

    public void put(String k, String s){
        put(new YamlBytes(k.getBytes()), new YamlBytes(s.getBytes()));
    }

    public void put(String k, List<?> l){
        put(new YamlBytes(k.getBytes()), new YamlArray(l));
    }

    public void put(String k, Map<?, ?> l){
        put(new YamlBytes(k.getBytes()), new YamlObject(l));
    }

    public void put(String k, YamlArray a){
        put(new YamlBytes(k.getBytes()), a);
    }

    public void put(String k, YamlObject o){
        put(new YamlBytes(k.getBytes()), o);
    }

    public YamlVariable valueOf(YamlBytes k){
        return m.get(k);
    }

    public Object get(String k){
        return m.get(new YamlBytes(k.getBytes())).getObject();
    }

    public Integer getInteger(String k){
        return ((Number) m.get(new YamlBytes(k.getBytes())).getObject()).intValue();
    }

    public Long getLong(String k){
        return ((Number) m.get(new YamlBytes(k.getBytes())).getObject()).longValue();
    }

    public Short getShort(String k){
        return ((Number) m.get(new YamlBytes(k.getBytes())).getObject()).shortValue();
    }

    public Double getDouble(String k){
        return ((Number) m.get(new YamlBytes(k.getBytes())).getObject()).doubleValue();
    }

    public Float getFloat(String k){
        return ((Number) m.get(new YamlBytes(k.getBytes())).getObject()).floatValue();
    }

    public String getString(String k){
        return new String((byte[]) m.get(new YamlBytes(k.getBytes())).getObject());
    }

    public byte[] getBytes(String k){
        return (byte[]) m.get(new YamlBytes(k.getBytes())).getObject();
    }

    public YamlArray getYamlArray(String k){
        return (YamlArray) m.get(new YamlBytes(k.getBytes()));
    }

    public YamlObject getYamlObject(String k){
        return (YamlObject) m.get(new YamlBytes(k.getBytes()));
    }

    public boolean containsKey(String s){
        return m.containsKey(new YamlBytes(s.getBytes()));
    }

    public boolean containsValue(Number n){
        return m.containsValue(new YamlNumber(n.toString()));
    }

    public boolean containsValue(String s){
        return m.containsValue(new YamlBytes(s.getBytes()));
    }

    public boolean containsValue(byte[] b){
        return m.containsValue(new YamlBytes(b));
    }

    public boolean containsValue(List<?> l){
        return m.containsValue(new YamlArray(l));
    }

    public boolean containsValue(Map<?, ?> m){
        return this.m.containsValue(new YamlObject(m));
    }

    public boolean containsValue(YamlArray a){
        return m.containsValue(a);
    }

    public boolean containsValue(YamlObject o){
        return m.containsValue(o);
    }

    public void remove(String k){
        YamlBytes b = new YamlBytes(k.getBytes());
        if(m.containsKey(b)){
            m.remove(b);
        }
    }

    public Set<YamlBytes> keySet(){
        return m.keySet();
    }

    public List<YamlVariable> values(){
        return new ArrayList<>(m.values());
    }

    public int size(){
        return m.size();
    }

    @Override
    public Map<String, ?> getObject(){
        HashMap<String, Object> h = new HashMap<>();
        for(YamlBytes k : m.keySet()){
            h.put(new String(k.getObject()), m.get(k).getObject());
        }
        return h;
    }

    @Override
    public int hashCode(){
        return 3;
    }

    @Override
    public String toString(){
        StringBuilder b = new StringBuilder("{\r\n");

        for(YamlBytes o : m.keySet()){
            String k = new String(o.getObject());

            if(m.get(o) instanceof YamlNumber){
                b.append("\t\033[0;31m"+k+"\033[0m:\033[0;33m"+((YamlNumber) m.get(o)).getObject()+"\033[0m\r\n");

            }else if(m.get(o) instanceof YamlBytes){
                if(Charset.forName("US-ASCII").newEncoder().canEncode(new String(((YamlBytes) m.get(o)).getObject()))){
                    b.append("\t\033[0;31m"+k+"\033[0m:\033[0;34m"+new String(((YamlBytes) m.get(o)).getObject(), StandardCharsets.UTF_8)+"\033[0m\r\n");

                }else{
                    b.append("\t\033[0;31m"+k+"\033[0m:\033[0;34m BASE64 { "+Base64.getEncoder().encodeToString(((YamlBytes) m.get(o)).getObject())+" }\033[0m\r\n");
                }

            }else if(m.get(o) instanceof YamlArray){
                b.append("\t\033[0;32m"+k+"\033[0m:"+((YamlArray) m.get(o)).toString().replaceAll("\\r?\\n", "\r\n\t")+"\r\n");

            }else if(m.get(o) instanceof YamlObject){
                b.append("\t\033[0;32m"+k+"\033[0m:"+((YamlObject) m.get(o)).toString().replaceAll("\\r?\\n", "\r\n\t")+"\r\n");
            }
        }

        return b+"}";
    }

    public byte[] encode(){
        return new Yamler().encode(this);
    }
}
