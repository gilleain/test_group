package layout;

import java.awt.geom.Rectangle2D;

public interface SimpleLayout {
    
    public Representation layout(Layoutable layoutable, Rectangle2D canvas);
    
}
