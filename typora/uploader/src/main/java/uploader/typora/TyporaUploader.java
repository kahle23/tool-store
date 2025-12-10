package uploader.typora;

import artoria.data.Dict;
import uploader.core.Uploader;

import java.util.List;

public class TyporaUploader implements Uploader {

    protected String nextPath() {
    }

    @Override
    public void upload(Dict configs, List<String> data) {
        String savePath = configs.getString("save-path");
        String pathPrefix = configs.getString("path-prefix");
    }

}
