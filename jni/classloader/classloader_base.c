/*
 * Copyright (C) 2016 王振华 (WANG Zhenhua) <i@jackwish.net>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3, as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranties of
 * MERCHANTABILITY, SATISFACTORY QUALITY, or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

static int local_value = 0;

int get_value(void)
{
    return local_value;
}

void set_value(int v)
{
    local_value = v;
}

void inc_value(void)
{
    local_value++;
}

void dec_value(void)
{
    local_value--;
}
