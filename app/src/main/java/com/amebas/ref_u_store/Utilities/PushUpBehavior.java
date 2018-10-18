package com.amebas.ref_u_store.Utilities;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

/**
 * Class that defines method for snackbars to push the whole screen up.
 *
 * To use, layout's topmost container must be a coordinator layout. Its immediate child is then
 * a constraint layout. Then, for the constraint layout, set the attribute 'app:layout_behavior'
 * as this class. Finally, inside of the activity that uses the layout, when creating the snackbar,
 * set its view as the coordinator layout.
 */
public class PushUpBehavior extends CoordinatorLayout.Behavior<ConstraintLayout>
{
    /**
     * Empty default constructor.
     * @param c      context in which it is created.
     * @param attrs  set of attributes.
     */
    public PushUpBehavior(Context c, AttributeSet attrs) { }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ConstraintLayout child, View dependency)
    {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ConstraintLayout child, View dependency)
    {
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }
}
