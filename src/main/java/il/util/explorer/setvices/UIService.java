package il.util.explorer.setvices;

import com.formdev.flatlaf.FlatDarculaLaf;
import il.util.explorer.ui.MainFrameWrap;
import il.util.explorer.ui.components.ErrorDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.swing.*;

import static java.util.concurrent.CompletableFuture.runAsync;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class UIService {
    @Autowired
    private MainFrameWrap mainFrameWrap;

    public void begin() {
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Throwable e) {
            showErrDialog(new RuntimeException("Failed to initialize LaF", e));
        }
        mainFrameWrap.showFrame();
    }

    public void showErrDialog(Throwable throwable) {
        runAsync(() -> {
            new ErrorDialog(throwable);
        });
    }
}
