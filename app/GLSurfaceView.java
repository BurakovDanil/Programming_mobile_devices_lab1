import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

    public MyGLSurfaceView(Context context) {
        super(context);

        // Выбор версии OpenGL ES 2.0
        setEGLContextClientVersion(2);

        // Устанавливаем Renderer, который будет рисовать объекты
        setRenderer(new MyGLRenderer());
    }
}
