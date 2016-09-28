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
