TastyTrucks [![Build Status](https://travis-ci.org/kirussell/TastyTrucks.svg?branch=master)](https://travis-ci.org/kirussell/TastyTrucks)
---
Simple app that helps you to find trucks with tasty food near you or near place you are interested in
This is an Android app with UI that contains:
- Map that shows markers with food trucks near the given location
- Search bar to find places and set the location in which area to show trucks

User can choose three different options to point the area to search trucks:
- My location button will use current location to show trucks
- Touch on map will use touched point to search trucks
- Search bar will allow to point to concrete place/address and search trucks around its place

Data sources
---
- Data for trucks locations took from San Francisco Data API, powered by Socrata (https://dev.socrata.com/foundry/data.sfgov.org/6a9r-agq8)
- Data to suggest addresses / places is provided through Google Places API for Android (https://developers.google.com/places/android-api/)

Architecture
---
Trying to hold architecture as modular as possible:
- Dagger helps to inject dependencies like location provider, rest api client.
They can be easily replaced or extended, i.e. to use another data source for foods
trucks data or another addresses base for suggestion
- MVP for Map screen added to decouple map view (actually activity) from base app logic.
Google maps not working with devices without Google Play Services,
so in case of distribution outside the Google Play we will need to replace map view
(for example, with https://github.com/airbnb/AirMapView)

Also this modularity makes code more testable, parts can be mocked and isolated.
Tests has to be executed periodically - Travis CI is configured for public github repo.

Google play services availability check is made in MapActivity. It could be the part of presenter, but in perspective Map can be replaced to get rid of google play services dependency.

Trade-offs, left out
---
- Using Google APIs and services for location/places/maps. In perspective app can be installed on devices without google play services. For prototype it is easier and faster to build with google APIs.
- Search settings will be a good addition. To set search radius, for example, or filter search by food items.
- Currently data requested for SF, so it is better to restrict search with SF bounds or even better to create API to retrieve data for different areas.
- Truck info card can be implemented with bottom sheets pattern.
- Mocking static methods from android (PowerMock, Robolectric) instead of code duplication (MyTextUtils).

Links
___
- Linkedin: https://linkedin.com/in/kirussell
- Lib for intro Garson: https://github.com/kirussell/Garson
TODO
Link to other code you're particularly proud of.
Link to to the hosted application where applicable.

