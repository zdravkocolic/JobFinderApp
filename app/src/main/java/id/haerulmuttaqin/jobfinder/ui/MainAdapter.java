package id.haerulmuttaqin.jobfinder.ui;

import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import id.haerulmuttaqin.jobfinder.R;
import id.haerulmuttaqin.jobfinder.data.entity.GithubJob;
import id.haerulmuttaqin.jobfinder.databinding.ItemJobBinding;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.RecyclerViewAdapter> {


    private List<GithubJob> data;
    private MainViewModel viewModel;

    public MainAdapter(List<GithubJob> data, MainViewModel viewModel) {

        this.viewModel =viewModel;
        this.data = data;
    }

    @NonNull
    @Override
    public MainAdapter.RecyclerViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemJobBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_job, parent, false);

        return new RecyclerViewAdapter(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.RecyclerViewAdapter holder, int position) {

        GithubJob items= data.get(position);
        if (items!=null){
            holder.bind(items, viewModel);
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public static class RecyclerViewAdapter extends RecyclerView.ViewHolder{
        ItemJobBinding binding;
        public RecyclerViewAdapter(@NonNull ItemJobBinding binding) {
            super(binding.getRoot());
            this.binding =binding;
        }

        void  bind(GithubJob data, MainViewModel viewModel){
            Glide.with(binding.photoPreview.getContext())
                    .load(data.getCompanyLogo())
                    .error(binding.photoPreview.getContext().getDrawable(R.drawable.ic_round_business_center_24))
            .transition(DrawableTransitionOptions.withCrossFade()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                   binding.progress.stopShimmer();
                   binding.progress.setVisibility(View.GONE);

                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                   binding.progress.stopShimmer();
                   binding.progress.setVisibility(View.GONE);

                    return false;
                }
            })
                .into(binding.photoPreview);

            binding.setItem(data);
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
        }

    }
}
