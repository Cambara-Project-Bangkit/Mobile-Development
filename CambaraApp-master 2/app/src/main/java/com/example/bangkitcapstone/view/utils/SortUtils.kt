package com.example.bangkitcapstone.view.utils

import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SimpleSQLiteQuery

object SortUtils {
    fun getSortedQuery(sortType: AccuracySortType): SupportSQLiteQuery {
        val simpleQuery = StringBuilder().append("SELECT * FROM accuracy_history")

        when (sortType) {
            AccuracySortType.HIGH_ACCURACY -> {
                simpleQuery.append(" ORDER BY accuracy DESC, timestamp DESC")
            }
            AccuracySortType.LOW_ACCURACY -> {
                simpleQuery.append(" ORDER BY accuracy ASC, timestamp DESC")
            }
        }

        return SimpleSQLiteQuery(simpleQuery.toString())
    }
}
