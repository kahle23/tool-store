package uploader.core;

import artoria.data.Dict;

import java.util.List;

public interface Uploader {

    void upload(Dict configs, List<String> data);

}
