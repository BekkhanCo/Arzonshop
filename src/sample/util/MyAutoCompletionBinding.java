package sample.util;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import javafx.collections.FXCollections;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyAutoCompletionBinding<T> extends AutoCompletionTextFieldBinding<T> {


    public MyAutoCompletionBinding(TextField textField, Callback<ISuggestionRequest, Collection<T>> suggestionProvider) {
        super(textField, suggestionProvider);
    }

    public MyAutoCompletionBinding(TextField textField, Callback<ISuggestionRequest, Collection<T>> suggestionProvider, StringConverter<T> converter) {
        super(textField, suggestionProvider, converter);
    }

    public static MyAutoCompletionBinding<String> bindAutoCompletion(TextField textField, List<String> list){

        MyAutoCompletionBinding<String> stringAutoCompletionBinding = new MyAutoCompletionBinding<>(textField, new Callback<ISuggestionRequest, Collection<String>>() {
            @Override
            public Collection<String> call(ISuggestionRequest iSuggestionRequest) {
                String key1 = iSuggestionRequest.getUserText().trim().toLowerCase();

                if (key1.isEmpty()){
                    return FXCollections.emptyObservableList();
                }
                List<String> newlist = new ArrayList<>();
                String[] keys = CyrillicToLatin.transliterate(key1).split(" ");
                for (String stovar : list) {

                    boolean b = true;
                    for (String key : keys) {
                        if (!CyrillicToLatin.transliterate(stovar).contains(key)){
                            b = false;
                            break;
                        }
                    }
                    if(b){
                        newlist.add(stovar);
                    }
                }
                return newlist;
            }
        });
        stringAutoCompletionBinding.setVisibleRowCount(10);
        stringAutoCompletionBinding.prefWidthProperty().bind(textField.widthProperty());

        return stringAutoCompletionBinding;
    }


    public static boolean checkKeys(String key, String str){
        String[] keys = CyrillicToLatin.transliterate(key).split(" ");
        for (String key1 : keys) {
            if (!CyrillicToLatin.transliterate(str).contains(key1)){
                return false;
            }
        }
        return true;
    }

    public static boolean checkKeys(String[] keys, String str){
        for (String key1 : keys) {
            if (!CyrillicToLatin.transliterate(str).contains(key1)){
                return false;
            }
        }
        return true;
    }
}
