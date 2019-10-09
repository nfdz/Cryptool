package io.github.nfdz.cryptool.views.cypher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.nfdz.cryptool.R


class CypherFragment : Fragment(), CypherView {

    companion object {
        @JvmStatic
        fun newInstance() = CypherFragment()
    }

//    private val presenter: HomePresenter by lazy { HomePresenterImpl(this, activity?.let { HomeInteractorImpl(it) }) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cypher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
//        presenter.onCreate()
    }

    override fun onDestroyView() {
//        presenter.onDestroy()
        super.onDestroyView()
    }

    private fun setupView() {

    }

}