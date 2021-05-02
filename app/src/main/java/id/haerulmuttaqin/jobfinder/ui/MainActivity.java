package id.haerulmuttaqin.jobfinder.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Insert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import id.haerulmuttaqin.jobfinder.R;
import id.haerulmuttaqin.jobfinder.base.BaseActivity;
import id.haerulmuttaqin.jobfinder.base.BaseViewModel;
import id.haerulmuttaqin.jobfinder.data.api.ConnectionServer;
import id.haerulmuttaqin.jobfinder.data.entity.GithubJob;
import id.haerulmuttaqin.jobfinder.data.storage.GithubJobRepository;
import id.haerulmuttaqin.jobfinder.databinding.ActivityMainBinding;
import id.haerulmuttaqin.jobfinder.ui.detail.DetailActivity;

public class MainActivity extends BaseActivity<ActivityMainBinding,MainViewModel>
    implements MainViewModel.Navigator{



        @Inject ConnectionServer server;
        @Inject GithubJobRepository repository;

        private ActivityMainBinding binding;
        private MainViewModel viewModel;

        @Override
        public int getBindingVariable () {
        return 0;
    }

        @Override
        public int getLayoutId () {
        return R.layout.activity_main;
    }

        @Override
        public MainViewModel getViewModel () {
        return viewModel;
    }

        @Override
        protected void onCreate (@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        viewModel = new ViewModelProvider(this, new MainViewModel.ModelFactory(this, server, repository))
                .get(MainViewModel.class);
        viewModel.setNavigator(this);
        viewModel.getJobsFromServer();
        viewModel.getLiveData().observe(this, githubJobs -> {
            binding.recyclerView.setAdapter(new MainAdapter(githubJobs,viewModel));
        });
            viewModel.getLiveDataMarked().observe(this, githubJobs -> {
                if (githubJobs.size() > 0 ) {
                    binding.markedTitle.setVisibility(View.VISIBLE);
                    binding.recyclerViewMarked.setVisibility(View.VISIBLE);
                    binding.recyclerViewMarked.setAdapter(new MainMarkedAdapter(githubJobs, viewModel));
                }else{
                    binding.markedTitle.setVisibility(View.GONE);
                    binding.recyclerViewMarked.setVisibility(View.GONE);

                }
            });
        binding.swipeRefresh.setOnRefreshListener(()->viewModel.getJobsFromServer());
    }

    @Override
    public void showProgress() {
        binding.swipeRefresh.setRefreshing(true);
        binding.emptyView.setVisibility(View.GONE);
        binding.shimmer.setVisibility(View.VISIBLE);
        binding.shimmer.startShimmer();

        binding.contentLayout.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        binding.swipeRefresh.setRefreshing(false);
        binding.emptyView.setVisibility(View.GONE);
        binding.shimmer.setVisibility(View.GONE);
        binding.shimmer.stopShimmer();

        binding.contentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void getResult(boolean status, String message) {
        if(!status){
            binding.emptyView.setVisibility(View.VISIBLE);
            binding.textEmptyErr.setText(message);
            binding.recyclerView.setVisibility(View.GONE);
        }else {
            binding.emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMark(int mark, String title) {
        Snackbar.make(binding.getRoot(), mark == 0?"\uD83D\uDE13 Unmarked " + title : "\uD83D\uDE0D Marked"
        , Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(GithubJob githubJob) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("item", githubJob);
        startActivity(intent);
    }
}
