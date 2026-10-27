package com.atom.vpn.data.remote.api

import retrofit2.http.GET

/**
 * VPN Gate public CSV endpoint.
 * Docs: https://www.vpngate.net/en/dev.aspx
 *
 * The CSV is semi-colon separated, with a base64-encoded OpenVPN config
 * in the last column for servers that support OpenVPN.
 *
 * Usage terms: only fetch on demand; implement a minimum 60-second
 * refresh interval to be a good API citizen.
 */
interface VpnGateApi {
    @GET("api/iphone/") // returns CSV starting with "*vpn_servers"
    suspend fun getServersCsv(): String
}
