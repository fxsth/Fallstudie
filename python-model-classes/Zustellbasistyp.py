'''
Klasse Zustellbasistyp

'''


class Zustellbasistyp:
    def __init__(self, id_int, typename, l_n, m_n, s_n):
        self.id_int = id_int
        self.typename = typename
        self.l_n = l_n
        self.m_n = m_n
        self.s_n = s_n

    def does_fit(self, l, m, s):
        l_spare = self.l_n - l
        if (l_spare < 0):
            return False
        m_spare = l_spare + self.m_n - m
        if (m_spare < 0):
            return False
        s_spare = m_spare + self.s_n - s
        if (s_spare < 0):
            return False
        return True


z1 = Zustellbasistyp(1, "Basistyp", 1, 5, 7)
print(z1.does_fit(0, 5, 8))
