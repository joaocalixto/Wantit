package neurotech.com.br.wantit.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by JoaoCalixto on 24/10/2015.
 */
public class LocaisUtil {

//    private static final String MINT = "FC:5A:29:87:82:DF";
//    private static final String blueberry = "EA:7A:88:67:FE:40";
//    private static final String ice = "E1:56:9B:61:8E:CB";

    HashMap<String, String> locaisEnderecos = new HashMap<String,String>();

    public LocaisUtil(){
        locaisEnderecos.put("JUMP BRASIL","R. Cap. Lima");
        locaisEnderecos.put("WANT.IT","EA:7A:88:67:FE:40");
        locaisEnderecos.put("LOJA ICE","E1:56:9B:61:8E:CB");
        locaisEnderecos.put("LOJA MINT","FC:5A:29:87:82:DF");
    }

    public String getPlace(String placeAddres){

        String[] split = placeAddres.split(",");

        Set<Map.Entry<String, String>> entries = locaisEnderecos.entrySet();

        for (Map.Entry<String, String> entry : locaisEnderecos.entrySet()){
            String add = entry.getValue();
            if(add.contains(split[0])){
                return entry.getKey();
            }
        }
        return locaisEnderecos.get(placeAddres);

    }

}
