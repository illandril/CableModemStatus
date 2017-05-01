# CableModemStatus
This project is a collection of Java classes for parsing the HTML from the status page of several different Cable Modems, and converting that HTML into a CSV for easy graphing.

I have used this to graph the upstream power levels over time to help convince Spectrum's Tech and Maintenance teams that my intermittent internet outages were not caused by my router or laptop, but were instead an issue in Spectrum's coax lines.

If I am provided with sample HTML files from other modems, I will add other modems (spare time permitting). I will also accept pull requests for other modems, corrections to the modems I already have, and/or enhancements to make this not only analyze HTML files on-disk but also actually make periodic requests to the status pages directly (I use a script run by crontab to get my modem's status page HTML to analyze currently).
