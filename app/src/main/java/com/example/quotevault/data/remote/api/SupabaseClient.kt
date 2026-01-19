package com.example.quotevault.data.remote.api

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseClientProvider @Inject constructor() {

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://loxrlkzjaamimgyhomzq.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxveHJsa3pqYWFtaW1neWhvbXpxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njg0MDc3MTQsImV4cCI6MjA4Mzk4MzcxNH0.mPEMWcLfoIkrHusOmqdqs73FgZzrRvGX4ag1AHQIEIQ"
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
    }
}