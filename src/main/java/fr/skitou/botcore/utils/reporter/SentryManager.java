/*
 * Copyright (C) 2021-2024 Ruben Rouvière, Chianti Gally, uku3lig, Rayan Malloul, and Maxandre Rochefort.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package fr.skitou.botcore.utils.reporter;

import fr.skitou.botcore.core.Config;
import io.sentry.Sentry;

public class SentryManager {
    private static SentryManager instance = null;

    private SentryManager() {
        Sentry.init(opt -> opt.setDsn(Config.CONFIG.getPropertyOrDefault("sentryDns")));
    }

    public static SentryManager getInstance() {
        if (instance == null) //noinspection InstantiationOfUtilityClass
            instance = new SentryManager();
        return instance;
    }
}
