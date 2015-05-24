In this project all the data is cached in DB once downloaded from server. Caching is done using
Observer pattern (using standard Android Loaders and BroadcastReceivers). Network utilities are
accessible from Singleton helper class.
Since some history data periods are intraday, we have to use different API for this occasions.
Plots are build with achartengine library, accessible from maven central repo.