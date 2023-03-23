Yamler
========

This is an implementation of YAML for Java. Bencode is used for Config files. Its a lightweight fast data serialization.

Benchmarks
-----
Here are some examples of this library compared to other major data serialization methods.

Serialization / Encoding
| Method  | Time in Mills |
| ------------- | ------------- |
| YAML  | 60  |
| Bencode  | 57  |
| JSON  | 230  |

Parsing
| Method  | Time in Mills |
| ------------- | ------------- |
| YAML  | 80  |
| Bencode  | 83  |
| JSON  | 281  |

Byte Size when encoded
| Method  | Bytes |
| ------------- | ------------- |
| YAML  | 30256091  |
| Bencode  | 10134606  |
| JSON  | 51538417  |

Library
-----
The JAR for the library can be found here: [YAML JAR](https://github.com/DrBrad/Yamler/blob/main/out/artifacts/Yamler_jar/Yamler.jar?raw=true)

Usage
-----
Here are some examples of how to use the Yamler library.

**Yaml Object | Map**
```Java
//FROM MAP
HashMap<String, String> l = new HashMap<>();
YamlObject yob = new YamlObject(l);

//FROM BYTES
byte[] b; //ARRAY OF BYTES
YamlObject yob = new YamlObject(b);

//CREATE YAML
YamlObject yob = new YamlObject();
```

**Put | Get data**
```Java
//ARRAY
yar.put(1000);
yar.get(0);

//MAP
yob.put("KEY", 100);
yob.get("KEY");
```

**Encoding to byte array**
```Java
yob.encode();
```

**Readable String**
```Java
System.out.println(yar.toString());
```
