package il.util.explorer.setvices;

import com.formdev.flatlaf.FlatDarculaLaf;
import il.util.explorer.ui.ErrorDialog;
import il.util.explorer.ui.FormUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;

@Service
public class UIService {
    @Autowired
    private FormUI form;

    public void begin() {
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Throwable e) {
            showErrDialog(new RuntimeException("Failed to initialize LaF", e));
        }
        form.show();
    }

    public void showErrDialog(Throwable throwable) {
        new ErrorDialog(throwable);
    }
}
