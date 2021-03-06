#!/usr/bin/env python

import unittest

import logging
logging.basicConfig(level=logging.INFO)

TESTS = []

# rndaemon tests
TESTS += ['.'.join(['plowapp.rndaemon.test', p]) for p in (
    'test_profile',
    'test_logging',
    'test_run.TestCommunications',
    'test_run.TestResourceManager',
    'test_run.TestProcessManager',
    )
]

def additional_tests():
    suite = unittest.TestSuite()
    suite.addTest(unittest.TestLoader().loadTestsFromNames(TESTS))
    return suite


if __name__ == "__main__":
    suite = additional_tests()
    unittest.TextTestRunner(verbosity=2).run(suite)
