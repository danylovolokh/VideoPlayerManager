package com.volokh.danylo.visibility_utils.items;

import android.view.View;

import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;

/**
 * A general interface for list items.
 * This interface is used by {@link ListItemsVisibilityCalculator}
 *
 * @author danylo.volokh
 */
public interface ListItem {
    /**
     * When this method is called, the implementation should provide a visibility percents in range 0 - 100 %
     * @param view the view which visibility percent should be calculated.
     *             Note: visibility doesn't have to depend on the visibility of a full view.
     *             It might be calculated by calculating the visibility of any inner View
     *
     * @return percents of visibility
     */
    int getVisibilityPercents(View view);

    /**
     * When view visibility become bigger than "current active" view visibility then the new view becomes active.
     * This method is called
     */
    void setActive(View newActiveView, int newActiveViewPosition);

    /**
     * There might be a case when not only new view becomes active, but also when no view is active.
     * When view should stop being active this method is called
     */
    void deactivate(View currentView, int position);
}
