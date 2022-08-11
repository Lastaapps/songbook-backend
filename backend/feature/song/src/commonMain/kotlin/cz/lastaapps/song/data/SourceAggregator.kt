package cz.lastaapps.song.data

import cz.lastaapps.song.domain.LoadSongDataSource
import cz.lastaapps.song.domain.SearchSongByAuthorDataSource
import cz.lastaapps.song.domain.SearchSongByNameDataSource
import cz.lastaapps.song.domain.SearchSongByTextDataSource
import cz.lastaapps.song.domain.model.search.OnlineSource
import cz.lastaapps.song.domain.sources.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal interface SourceAggregator {
    fun getSearchByName(source: OnlineSource): SearchSongByNameDataSource?
    fun getSearchByText(source: OnlineSource): SearchSongByTextDataSource?
    fun getSearchByAuthor(source: OnlineSource): SearchSongByAuthorDataSource?

    val searchByName: ImmutableList<SearchSongByNameDataSource>
    val searchByText: ImmutableList<SearchSongByTextDataSource>
    val searchByAuthor: ImmutableList<SearchSongByAuthorDataSource>

    fun getLoader(source: OnlineSource): LoadSongDataSource
}

internal class SourceAggregatorImpl : SourceAggregator, KoinComponent {

    private val agama by lazy { get<AgamaDataSource>() }
    private val brnkni by lazy { get<BrnkniDataSource>() }
    private val pisnickyAkordy by lazy { get<PisnickyAkordyDataSource>() }
    private val supermusic by lazy { get<SuperMusicDataSource>() }
    private val velkyZpevnik by lazy { get<VelkyZpevnikDataSource>() }
    private val zpevnikSAkordy by lazy { get<ZpevnikSAkordyDataSource>() }

    override fun getSearchByName(source: OnlineSource): SearchSongByNameDataSource =
        when (source) {
            OnlineSource.Agama -> agama
            OnlineSource.Brnkni -> brnkni
            OnlineSource.PisnickyAkordy -> pisnickyAkordy
            OnlineSource.SuperMusic -> supermusic
            OnlineSource.VelkyZpevnik -> velkyZpevnik
            OnlineSource.ZpevnikSAkordy -> zpevnikSAkordy
        }

    override fun getSearchByText(source: OnlineSource): SearchSongByTextDataSource? =
        when (source) {
            OnlineSource.Agama -> agama
            OnlineSource.SuperMusic -> supermusic
            OnlineSource.VelkyZpevnik -> velkyZpevnik

            OnlineSource.Brnkni,
            OnlineSource.PisnickyAkordy,
            OnlineSource.ZpevnikSAkordy -> null
        }

    override fun getSearchByAuthor(source: OnlineSource): SearchSongByAuthorDataSource =
        when (source) {
            OnlineSource.Agama -> agama
            OnlineSource.Brnkni -> brnkni
            OnlineSource.PisnickyAkordy -> pisnickyAkordy
            OnlineSource.SuperMusic -> supermusic
            OnlineSource.VelkyZpevnik -> velkyZpevnik
            OnlineSource.ZpevnikSAkordy -> zpevnikSAkordy
        }

    private val all by lazy {
        listOf(agama, brnkni, pisnickyAkordy, supermusic, velkyZpevnik, zpevnikSAkordy)
    }

    override val searchByName: ImmutableList<SearchSongByNameDataSource> by lazy {
        all.filterIsInstance<SearchSongByNameDataSource>().toImmutableList()
    }
    override val searchByText: ImmutableList<SearchSongByTextDataSource> by lazy {
        all.filterIsInstance<SearchSongByTextDataSource>().toImmutableList()
    }
    override val searchByAuthor: ImmutableList<SearchSongByAuthorDataSource> by lazy {
        all.filterIsInstance<SearchSongByAuthorDataSource>().toImmutableList()
    }

    override fun getLoader(source: OnlineSource): LoadSongDataSource =
        when (source) {
            OnlineSource.Agama -> agama
            OnlineSource.Brnkni -> brnkni
            OnlineSource.PisnickyAkordy -> pisnickyAkordy
            OnlineSource.SuperMusic -> supermusic
            OnlineSource.VelkyZpevnik -> velkyZpevnik
            OnlineSource.ZpevnikSAkordy -> zpevnikSAkordy
        }
}
