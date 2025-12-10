package uploader;

import artoria.data.Dict;
import artoria.data.KeyValuePair;
import artoria.data.Pair;
import artoria.exception.ExceptionUtils;
import artoria.util.Assert;
import uploader.core.Uploader;
import uploader.typora.TyporaUploader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Application entry.
 * @author Kahle
 */
public class UploaderApplication {
    private static final Map<String, Uploader> UPLOADER_MAP = new ConcurrentHashMap<String, Uploader>();
    static {
        UPLOADER_MAP.put("typora-local", new TyporaUploader());
    }

    public static void main(String[] args) {
        boolean debug = false;
        try {
            // 解析参数
            Pair<Dict, List<String>> pair = parseArgs(args);
            List<String> data = pair.getRight();
            Dict configs = pair.getLeft();
            // 是否展示具体的错误信息
            debug = configs.getBoolean("debug", false);
            // 使用哪种模式，对应着具体的实现类
            String mode = configs.getString("mode");
            Assert.notBlank(mode, "Parameter \"mode\" must not blank. ");
            Uploader uploader = UPLOADER_MAP.get(mode);
            uploader.upload(configs, data);
        }
        catch (Exception e) {
            System.err.println("An unexpected error. ");
            System.err.println(debug ? ExceptionUtils.toString(e) : e.getMessage());
        }
    }

    private static Pair<Dict, List<String>> parseArgs(String[] args) {
        KeyValuePair<Dict, List<String>> result = new KeyValuePair<Dict, List<String>>();
        List<String> list = new ArrayList<String>();
        Dict dict = Dict.of();
        result.setValue(list);
        result.setKey(dict);
        for (String arg : args) {
            if (!arg.startsWith("--")) {
                list.add(arg);
                continue;
            }
            arg = arg.substring(2);
            String[] split = arg.split("=");
            if (split.length < 2) {
                dict.put(split[0], null);
            } else {
                dict.put(split[0], split[1]);
            }
        }
        return result;
    }

}
