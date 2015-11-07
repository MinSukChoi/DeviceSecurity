// Generated code from Butter Knife. Do not modify!
package soma.devicesecurity.studybrowser;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends soma.devicesecurity.studybrowser.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492943, "field 'webView'");
    target.webView = finder.castView(view, 2131492943, "field 'webView'");
    view = finder.findRequiredView(source, 2131492941, "field 'editText_address'");
    target.editText_address = finder.castView(view, 2131492941, "field 'editText_address'");
    view = finder.findRequiredView(source, 2131492944, "field 'imageView_block'");
    target.imageView_block = finder.castView(view, 2131492944, "field 'imageView_block'");
    view = finder.findRequiredView(source, 2131492942, "method 'move'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.move();
        }
      });
  }

  @Override public void unbind(T target) {
    target.webView = null;
    target.editText_address = null;
    target.imageView_block = null;
  }
}
