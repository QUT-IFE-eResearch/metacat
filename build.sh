#!/bin/sh
#
# Build script to call the ant build system
#
#   '$RCSfile$'
#     Authors: Matt Jones
#   Copyright: 2000 Regents of the University of California and the
#              National Center for Ecological Analysis and Synthesis
# For Details: http://www.nceas.ucsb.edu/
#
#    '$Author: jones $'
#      '$Date: 2001-01-19 05:55:33 +1000 (Fri, 19 Jan 2001) $'
#  '$Revision: 670 $'
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

umask 002; ant $*
